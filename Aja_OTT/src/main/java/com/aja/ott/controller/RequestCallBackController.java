package com.aja.ott.controller;
import com.aja.ott.service.RequestCallBackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aja.ott.entity.RequestCallBack;

@RestController
@RequestMapping
@CrossOrigin(origins = "*")
public class RequestCallBackController {

    private final RequestCallBackService requestCallBackService;
	@Autowired
private RequestCallBackService requestService;

    RequestCallBackController(RequestCallBackService requestCallBackService) {
        this.requestCallBackService = requestCallBackService;
    }
	@PostMapping("/save-request-call-back")
	public String saveRequestCall(@RequestBody RequestCallBack requestCall ) {
		requestService.saveRequest(requestCall);
	return "Request Call saved successfully!";

	}
	
	@GetMapping("/get-all-request-calls")
	public Page<RequestCallBack> getAllRequests(
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size) {

	    return requestService.getAllRequests(page, size);
	}
}
