package com.aja.ott.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import com.aja.ott.entity.RequestCallBack;
import com.aja.ott.repository.RequestCallBackRepository;

@Service
public class RequestCallBackService {
	@Autowired
	private RequestCallBackRepository requestCallBackRepository;
	
	public void saveRequest(RequestCallBack requestCall) {
		 requestCallBackRepository.save(requestCall);
	}
	
    public boolean softDelete(Long id) {
        Optional<RequestCallBack> record = requestCallBackRepository.findById(id);
        if (record.isPresent()) {
            RequestCallBack call = record.get();
            if(call.isDeleted()) {
            	return false;
            }
            call.setDeleted(true);
            requestCallBackRepository.save(call);
            return true;
        }
        return false;
    }
	
	public Page<RequestCallBack> getAllRequests(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return requestCallBackRepository.findByAndIsDeletedFalse(pageable);
    }

}
