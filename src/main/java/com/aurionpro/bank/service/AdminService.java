package com.aurionpro.bank.service;

import com.aurionpro.bank.dto.AdminDto;
import com.aurionpro.bank.dto.PageResponse;

public interface AdminService {
	 PageResponse<AdminDto> getAllAdmins(int page, int size);
	 AdminDto getAdminById(Long id);
}

