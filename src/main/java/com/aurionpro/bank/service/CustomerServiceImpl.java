package com.aurionpro.bank.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.aurionpro.bank.dto.AccountDto;
import com.aurionpro.bank.dto.AccountDtoForDisplay;
import com.aurionpro.bank.dto.CustomerDto;
import com.aurionpro.bank.dto.CustomerWithBalanceDto;
import com.aurionpro.bank.dto.KycDocumentDto;
import com.aurionpro.bank.dto.PageResponse;
import com.aurionpro.bank.dto.PassbookDto;
import com.aurionpro.bank.dto.TransactionDto;
import com.aurionpro.bank.entity.Account;
import com.aurionpro.bank.entity.Customer;
import com.aurionpro.bank.entity.DocumentType;
import com.aurionpro.bank.entity.KycDocument;
import com.aurionpro.bank.entity.KycStatus;
import com.aurionpro.bank.entity.Transaction;
import com.aurionpro.bank.entity.TransactionType;
import com.aurionpro.bank.repository.AccountRepository;
import com.aurionpro.bank.repository.CustomerRepository;
import com.aurionpro.bank.repository.KycDocumentRepository;
import com.aurionpro.bank.repository.TransactionRepository;
import com.aurionpro.bank.security.AuthUtil;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountServiceImpl accountServiceImpl;
    
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private KycDocumentRepository kycRepository;
    
    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Override
    public PageResponse<CustomerDto> getAllCustomers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Customer> customerPage = customerRepository.findAll(pageable);

        logger.info("Retrieved page {} of customers with size {}", page, size);
        logger.debug("Total customers found: {}", customerPage.getTotalElements());

        return new PageResponse<>(
                customerPage.getTotalPages(),
                customerPage.getTotalElements(),
                size,
                customerPage.getContent().stream().map(this::toCustomerDtoMapper).toList(),
                !customerPage.hasNext()
        );
    }
    
    @Override
    public List<CustomerWithBalanceDto> getAllCustomersWithBalances() {
        logger.info("Fetching all customers with their balances");

        List<Customer> customers = customerRepository.findAll();
        List<CustomerWithBalanceDto> customersWithBalances = customers.stream()
            .map(customer -> new CustomerWithBalanceDto(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getAccounts().stream()
                    .map(account -> new AccountDto(
                        account.getId(),
                        account.getAccountNumber(),
                        account.getBalance()
                    ))
                    .collect(Collectors.toList())
            ))
            .collect(Collectors.toList());

        logger.debug("Total customers with balances retrieved: {}", customersWithBalances.size());
        return customersWithBalances;
    }

    @Override
    public CustomerWithBalanceDto getCustomerWithBalancesById(Long customerId) {
        // Fetch the currently logged-in user's customer ID
    	Customer loggedInCustomer = authUtil.getAuthenticatedCustomer();
        Long loggedInCustomerId = loggedInCustomer.getId();
        
        if (!customerId.equals(loggedInCustomerId)) {
            throw new AccessDeniedException("You can only access your own account.");
        }
        
        logger.info("Fetching customer with ID: {}", customerId);
        
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> {
                logger.error("Customer with ID {} not found", customerId);
                return new EntityNotFoundException("Customer not found with ID: " + customerId);
            });
        
        return new CustomerWithBalanceDto(
            customer.getId(),
            customer.getFirstName(),
            customer.getLastName(),
            customer.getAccounts().stream()
                .map(account -> new AccountDto(
                    account.getId(),
                    account.getAccountNumber(),
                    account.getBalance()
                ))
                .collect(Collectors.toList())
        );
    }
    
    @Override
    public List<TransactionDto> getTransactionsByCustomerId(Long customerId) {
        // Fetch the currently logged-in user's customer ID
       	Customer loggedInCustomer = authUtil.getAuthenticatedCustomer();
        Long loggedInCustomerId = loggedInCustomer.getId();
        
        if (!customerId.equals(loggedInCustomerId)) {
            throw new AccessDeniedException("You can only access transactions for your own account.");
        }
        
        logger.info("Fetching transactions for customer ID: {}", customerId);
        
        List<Transaction> transactions = transactionRepository.findByAccountCustomerId(customerId);
        return transactions.stream()
                .map(this::toTransactionDto)
                .collect(Collectors.toList());
    }

    private TransactionDto toTransactionDto(Transaction transaction) {
        return new TransactionDto(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getDate(),
                transaction.getType(),
                transaction.getSenderAccount() != null ? transaction.getSenderAccount().getAccountNumber() : null,
                transaction.getReceiverAccount() != null ? transaction.getReceiverAccount().getAccountNumber() : null
        );
    }

    private CustomerDto toCustomerDtoMapper(Customer customer) {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setCustomerId(customer.getId());
        customerDto.setFirstName(customer.getFirstName());
        customerDto.setLastName(customer.getLastName());
        List<AccountDtoForDisplay> accountDtos = customer.getAccounts().stream()
                .map(accountServiceImpl::toAccountDtoMapper)
                .toList();
        customerDto.setAccounts(accountDtos);
        
        return customerDto;
    }

    @Override
    public CustomerDto getCustomerById(Long customerId) {
        logger.info("Fetching customer by ID: {}", customerId);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    logger.error("Customer with ID {} not found", customerId);
                    return new EntityNotFoundException("Customer not found");
                });

        CustomerDto customerDto = toCustomerDtoMapper(customer);
        logger.info("Found customer: {}", customerDto);
        return customerDto;
    }
    
    @Override
    @Transactional
    public void deactivateCustomer(Long customerId) {
        logger.info("Deactivating customer with ID: {}", customerId);

        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> {
                logger.error("Customer with ID {} not found", customerId);
                return new EntityNotFoundException("Customer not found with ID: " + customerId);
            });

        if (!customer.isActive()) {
            throw new IllegalStateException("Customer account is already deactivated");
        }

        customer.setActive(false); // Deactivate customer by setting the active flag to false
        customerRepository.save(customer);
        
        logger.info("Customer with ID: {} has been deactivated", customerId);
    }

    @Autowired
    private KycDocumentRepository kycDocumentRepository;

 
    @Autowired
    private CloudinaryService cloudinaryService;

    @Override
    public KycDocumentDto uploadKycDocument(Long customerId, DocumentType documentType, MultipartFile file) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NullPointerException("Customer not found with ID: " + customerId));

        // Validate file
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty");
        }

        // Upload the document to Cloudinary
        String documentUrl;
        try {
            documentUrl = cloudinaryService.uploadDocument(file);
        } catch (IOException e) {
            logger.error("Error uploading document to Cloudinary", e);
            throw new RuntimeException("Error uploading document. Please try again later.");
        }

        // Create a new KycDocument entity
        KycDocument kycDocument = new KycDocument();
        kycDocument.setCustomer(customer);
        kycDocument.setAccount(customer.getAccounts().get(0)); // Assuming default first account
        kycDocument.setDocumentType(documentType); 
        kycDocument.setDocumentUrl(documentUrl);
        kycDocument.setKycStatus(KycStatus.PENDING);

        // Save the document in the database
        KycDocument savedDocument = kycDocumentRepository.save(kycDocument);

        return new KycDocumentDto(
                savedDocument.getId(),
                savedDocument.getDocumentType(),
                savedDocument.getDocumentUrl(),
                savedDocument.getKycStatus()
        );
    }


    @Override
    public KycStatus getKycStatus(Long customerId) {
        KycDocument kycDocument = kycDocumentRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new NullPointerException("KYC Document not found for customer ID: " + customerId));
        return kycDocument.getKycStatus();
    }

    @Override
    public KycDocumentDto getKycDocument(Long customerId) {
        KycDocument kycDocument = kycDocumentRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new NullPointerException("KYC Document not found for customer ID: " + customerId));
        return new KycDocumentDto(kycDocument.getId(), kycDocument.getDocumentType(), kycDocument.getDocumentUrl(),
                kycDocument.getKycStatus());
    }
    @Override
    public PassbookDto getPassbook(Long customerId) {
        // Fetch the currently logged-in user's customer ID
        Customer loggedInCustomer = authUtil.getAuthenticatedCustomer();
        Long loggedInCustomerId = loggedInCustomer.getId();
        
        if (!customerId.equals(loggedInCustomerId)) {
            throw new AccessDeniedException("You can only access your own passbook.");
        }
        
        // Retrieve accounts for the customer
        List<Account> accounts = accountRepository.findByCustomerId(customerId);
        if (accounts.isEmpty()) {
            throw new EntityNotFoundException("No accounts found for customer ID: " + customerId);
        }

        // Prepare PassbookDto
        PassbookDto passbookDto = new PassbookDto();
        List<PassbookDto.TransactionDto> allTransactionDtos = new ArrayList<>();

        // Iterate over each account
        for (Account account : accounts) {
            // Set the account number for the first account (if needed, or modify to include multiple account numbers)
            if (passbookDto.getAccountNumber() == null) {
                passbookDto.setAccountNumber(account.getAccountNumber());
            }

            // Retrieve transactions for the account
            List<Transaction> transactions = transactionRepository.findByAccountId(account.getId());
            List<PassbookDto.TransactionDto> transactionDtos = transactions.stream()
                .map(transaction -> {
                    String senderAccount = null;
                    String receiverAccount = null;
                    
                    if (transaction.getType() == TransactionType.TRANSFER) {
                        senderAccount = transaction.getSenderAccount().getAccountNumber();
                        receiverAccount = transaction.getReceiverAccount().getAccountNumber();
                    }

                    return new PassbookDto.TransactionDto(
                        transaction.getDate().toString(),  // Adjust format as needed
                        transaction.getAmount(),
                        transaction.getType(),
                        senderAccount,
                        receiverAccount
                    );
                })
                .collect(Collectors.toList());

            // Add the transactions for this account to the overall list
            allTransactionDtos.addAll(transactionDtos);
        }

        // Set all transactions in the passbook
        passbookDto.setTransactions(allTransactionDtos);

        // Generate CSV file from PassbookDto
        File csvFile = generateCsvFile(passbookDto);
        
        // Convert CSV file to PDF
        File pdfFile = convertCsvToPdf(passbookDto);
        
        // Send email with PDF attachment
        emailService.sendPassbookEmail(loggedInCustomer.getUser().getUsername(), pdfFile);
        
        return passbookDto;
    }

    private File generateCsvFile(PassbookDto passbookDto) {
        String fileName = "passbook_" + LocalDate.now() + ".csv";
        File csvFile = new File(fileName);

        try (FileWriter writer = new FileWriter(csvFile)) {
            // Write header
            writer.append("Date,Amount,Transaction Type\n");

            // Write transaction data
            for (PassbookDto.TransactionDto transaction : passbookDto.getTransactions()) {
                writer.append(transaction.getDate())
                      .append(",")
                      .append(transaction.getAmount().toString())
                      .append(",")
                      .append(transaction.getTransactionType().name())
                      .append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while generating CSV file", e);
        }

        return csvFile;
    }
    private File convertCsvToPdf(PassbookDto passbookDto) {
        String pdfFileName = "passbook_" + LocalDate.now() + ".pdf";
        File pdfFile = new File(pdfFileName);

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page, AppendMode.OVERWRITE, true)) {
                PDType1Font fontBold = PDType1Font.HELVETICA_BOLD;
                PDType1Font fontRegular = PDType1Font.HELVETICA;
                float margin = 50;
                float yStart = 750;
                float tableHeight = 15;
                float rowHeight = 20;
                float tableWidth = 500;
                float yPosition = yStart;

                // Add Title
                contentStream.setFont(fontBold, 16);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Passbook Report");
                contentStream.endText();

                yPosition -= 40;

                // Add Account Number
                contentStream.setFont(fontRegular, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Account Number: " + passbookDto.getAccountNumber());
                contentStream.endText();

                yPosition -= 30;

                // Draw Table Header
                contentStream.setFont(fontBold, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Date     | Amount   | Transaction Type | Sender Account | Receiver Account");
                contentStream.endText();

                yPosition -= tableHeight;

                // Draw Table Rows
                contentStream.setFont(fontRegular, 12);
                for (PassbookDto.TransactionDto transaction : passbookDto.getTransactions()) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin, yPosition);
                    String line = String.format("%s | %.2f | %s | %s | %s",
                            transaction.getDate(),
                            transaction.getAmount(),
                            transaction.getTransactionType(),
                            transaction.getSenderAccountNumber() != null ? transaction.getSenderAccountNumber() : "",
                            transaction.getReceiverAccountNumber() != null ? transaction.getReceiverAccountNumber() : ""
                    );
                    contentStream.showText(line);
                    contentStream.endText();
                    yPosition -= rowHeight;
                }
            }

            document.save(pdfFile);
        } catch (IOException e) {
            throw new RuntimeException("Error while converting CSV to PDF", e);
        }

        return pdfFile;
    }


    
    

}
