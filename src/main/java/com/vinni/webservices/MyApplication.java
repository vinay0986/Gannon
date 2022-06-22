package com.vinni.webservices;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/rest")
public class MyApplication extends Application {
	private Set<Object> singletons = new HashSet<>();

	public MyApplication() {
		this.singletons.add(new RegistrationService());
		this.singletons.add(new ApprovedUsersService());
		this.singletons.add(new ChangePasswordService());
		this.singletons.add(new ForgotPasswordService());
		this.singletons.add(new LoginService());
		this.singletons.add(new DenyUsersListService());
		this.singletons.add(new ProfileService());
		this.singletons.add(new AuctionOrDonationService());
		this.singletons.add(new AllAuctionDonationsService());
		this.singletons.add(new UserWinsList());
	}

	@Override
	public Set<Object> getSingletons() {
		return this.singletons;
	}
}
