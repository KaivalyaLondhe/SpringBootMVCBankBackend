package com.aurionpro.bank.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.aurionpro.bank.dto.AdminDto;
import com.aurionpro.bank.dto.KycDocumentDto;
import com.aurionpro.bank.dto.KycDocumentUpdateRequestDto;
import com.aurionpro.bank.dto.PageResponse;
import com.aurionpro.bank.entity.Admin;
import com.aurionpro.bank.entity.Customer;
import com.aurionpro.bank.entity.KycDocument;
import com.aurionpro.bank.entity.KycStatus;
import com.aurionpro.bank.repository.AdminRepository;
import com.aurionpro.bank.repository.CustomerRepository;
import com.aurionpro.bank.repository.KycDocumentRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class AdminServiceImpl implements AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public PageResponse<AdminDto> getAllAdmins(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Admin> adminPage = adminRepository.findAll(pageable);

        logger.info("Retrieved page {} of admins with size {}", page, size);
        logger.debug("Total admins found: {}", adminPage.getTotalElements());

        return new PageResponse<>(
                adminPage.getTotalPages(),
                adminPage.getTotalElements(),
                size,
                adminPage.getContent().stream().map(this::toAdminDtoMapper).toList(),
                !adminPage.hasNext()
        );
    }

    @Override
    public AdminDto getAdminById(Long adminId) {
        logger.info("Fetching admin with ID: {}", adminId);
        
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> {
                    logger.error("Admin with ID {} not found", adminId);
                    return new EntityNotFoundException("Admin not found");
                });
        
        AdminDto adminDto = toAdminDtoMapper(admin);
        logger.info("Found admin: {}", adminDto);
        return adminDto;
    }
    
    private AdminDto toAdminDtoMapper(Admin admin) {
        AdminDto adminDto = new AdminDto();
        adminDto.setAdminId(admin.getId());
        adminDto.setFirstName(admin.getFirstName());
        adminDto.setLastName(admin.getLastName());
        return adminDto;
    }

    private Admin toAdminMapper(AdminDto adminDto) {
        Admin admin = new Admin();
        admin.setFirstName(adminDto.getFirstName());
        admin.setLastName(adminDto.getLastName());
        return admin;
    }
    @Autowired
    private KycDocumentRepository kycDocumentRepository;
    
    @Override
    public List<KycDocumentDto> getKycDocumentsByCustomerId(Long customerId) {
        // Fetch all KYC documents associated with the customer
        List<KycDocument> kycDocuments = kycDocumentRepository.findAllByCustomerId(customerId);
        
        // Throw an exception if no documents are found
        if (kycDocuments.isEmpty()) {
            throw new EntityNotFoundException("No KYC documents found for the customer with ID: " + customerId);
        }

        // Convert the list of KycDocument entities to KycDocumentDto objects
        List<KycDocumentDto> kycDocumentDtos = kycDocuments.stream()
            .map(kycDocument -> {
                KycDocumentDto dto = new KycDocumentDto();
                dto.setId(kycDocument.getId());
                dto.setDocumentUrl(kycDocument.getDocumentUrl());
                dto.setDocumentType(kycDocument.getDocumentType());
                dto.setKycStatus(kycDocument.getKycStatus());
                return dto;
            })
            .toList();

        // Return the list of DTOs
        return kycDocumentDtos;
    }

    
    @Override
    public void updateKycStatuses(Long customerId, List<KycDocumentUpdateRequestDto> documents) {
        // Loop through each document in the list
        for (KycDocumentUpdateRequestDto documentRequest : documents) {
            Long documentId = documentRequest.getDocumentId();
            KycStatus status = documentRequest.getStatus();
            
            // Fetch the KYC document by document ID and ensure it belongs to the customer
            KycDocument kycDocument = kycDocumentRepository.findById(documentId)
                    .orElseThrow(() -> new NullPointerException("Document not found for the given ID"));
            
            if (!kycDocument.getCustomer().getId().equals(customerId)) {
                throw new IllegalArgumentException("Document does not belong to the specified customer");
            }

            // Update the KYC status of the document
            kycDocument.setKycStatus(status);
            kycDocumentRepository.save(kycDocument);

            logger.info("KYC document ID {} updated for customer ID {} with status {}", documentId, customerId, status);
        }

        // Fetch the customer associated with these documents
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NullPointerException("Customer not found"));

        // Check if all KYC documents for this customer are approved
        List<KycDocument> customerDocuments = kycDocumentRepository.findAllByCustomerId(customerId);
        boolean allApproved = customerDocuments.stream()
            .allMatch(doc -> doc.getKycStatus() == KycStatus.APPROVED);

        // If all documents are approved, update the customer's KYC status to approved
        if (allApproved) {
            customer.setKycStatus(KycStatus.APPROVED); // Assuming you have this field in Customer entity
            customerRepository.save(customer);
            logger.info("Customer with ID {} has all KYC documents approved. KYC status updated to APPROVED.", customerId);
        }

        // Prepare and send an email for each document update
        for (KycDocumentUpdateRequestDto documentRequest : documents) {
            String subject = "KYC Status Update";
            String body = String.format(
                "Dear %s,\n\nYour KYC document with ID: %s has been updated to: %s.\n\n" +
                "Best Regards,\n" +
                "Your Bank",
                customer.getFirstName(), documentRequest.getDocumentId(), documentRequest.getStatus().name()
            );

            // Send email to the customer
            emailService.sendSimpleEmail(customer.getUser().getUsername(), subject, body);
        }

        // If all documents are approved, send an additional email for overall KYC approval
        if (allApproved) {
            String overallApprovalSubject = "KYC Fully Approved";
            String overallApprovalBody = String.format(
                "Dear %s,\n\nAll your KYC documents have been approved. Your overall KYC status is now: APPROVED.\n\n" +
                "Thank you for your cooperation.\n\n" +
                "Best Regards,\n" +
                "Your Bank",
                customer.getFirstName()
            );
            emailService.sendSimpleEmail(customer.getUser().getUsername(), overallApprovalSubject, overallApprovalBody);
        }
    }





}
