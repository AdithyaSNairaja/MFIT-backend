package com.aja.ott.configuration;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.aja.ott.entity.User;

public class UserPrincipal implements UserDetails {

	private User user;

	public UserPrincipal(com.aja.ott.entity.User user2) {

		this.user = user2;

	}

	@Override

	public Collection<? extends GrantedAuthority> getAuthorities() {

		return Collections.singleton(new SimpleGrantedAuthority("USER"));

	}

	@Override

	public String getPassword() {

		return user.getPassword();

	}

	@Override

	public String getUsername() {

		return user.getFirstName();

	}

	@Override

	public boolean isAccountNonExpired() {

		return UserDetails.super.isAccountNonExpired();

	}

	@Override

	public boolean isAccountNonLocked() {

		return true;

	}

	@Override

	public boolean isCredentialsNonExpired() {

		return true;

	}

	@Override

	public boolean isEnabled() {

		return true;

	}

}
