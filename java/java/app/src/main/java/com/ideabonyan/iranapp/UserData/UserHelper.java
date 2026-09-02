package com.ideabonyan.iranapp.UserData;


import android.content.Context;
import android.util.Log;


public class UserHelper {
	public static void SaveUserInfo(User userInfo, Context context)
	{
		UserSessionManager userSessionManager=new UserSessionManager(context);
		userSessionManager.setUser(userInfo.getuser_id(),userInfo.getFirstname(),userInfo.getLastname(),userInfo.getPhone(),
				/*userInfo.getReferral_code(),userInfo.getGuild_id(),*/userInfo.getIsVerrified());

	}
	
	public static User LoadUserInfo(Context context)
	{
		UserSessionManager userSessionManager=new UserSessionManager(context);
		User user=userSessionManager.getUser();
		user.setIsVerrified(userSessionManager.getIs_varryfy());
//		Log.v("varrfy",userSessionManager.getIs_varryfy());

		return user;
		
	}
	
	public static void RemoveUserInfo(Context context)
	{
		UserSessionManager userSessionManager=new UserSessionManager(context);
		userSessionManager.setUser(null,null,null,null,null);
	}
}
