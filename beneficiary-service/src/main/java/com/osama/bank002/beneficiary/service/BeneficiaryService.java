package com.osama.bank002.beneficiary.service;

import com.osama.bank002.beneficiary.domain.dto.SaveBeneficiaryRequest;
import com.osama.bank002.beneficiary.domain.dto.SavedBeneficiaryDto;

import java.util.List;

public interface BeneficiaryService {

    String currentUserId();

    SavedBeneficiaryDto save(SaveBeneficiaryRequest req);

    List<SavedBeneficiaryDto> listMine();

    void deleteById(Long id);

    void deleteByAccountNumber(String accountNumber);

}