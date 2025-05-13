package com.aja.ott.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aja.ott.entity.RequestCallBack;
@Repository
public interface RequestCallBackRepository  extends JpaRepository<RequestCallBack, Long>{

}
