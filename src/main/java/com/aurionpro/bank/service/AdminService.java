package com.aurionpro.bank.service;

import java.util.List;

import com.aurionpro.bank.dto.AdminDto;
import com.aurionpro.bank.dto.KycDocumentDto;
import com.aurionpro.bank.dto.KycDocumentUpdateRequestDto;
import com.aurionpro.bank.dto.PageResponse;
import com.aurionpro.bank.entity.KycStatus;

public interface AdminService {
	 PageResponse<AdminDto> getAllAdmins(int page, int size);
	 AdminDto getAdminById(Long id);
	

	   
		List<KycDocumentDto> getKycDocumentsByCustomerId(Long customerId);
		
		void updateKycStatuses(Long customerId, List<KycDocumentUpdateRequestDto> documents);
}

