package com.aurionpro.bank.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.aurionpro.bank.dto.AdminDto;
import com.aurionpro.bank.dto.PageResponse;
import com.aurionpro.bank.entity.Admin;
import com.aurionpro.bank.repository.AdminRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class AdminServiceImpl implements AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Autowired
    private AdminRepository adminRepository;

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
}
