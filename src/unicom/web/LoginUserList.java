package unicom.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import unicom.bo.AccountInfo;

public class LoginUserList {
	private static final Log log = LogFactory.getLog(LoginUserList.class);
	private static LoginUserList instance;
	
	private Map<String , AccountInfo> accountInfos = new HashMap<String , AccountInfo>();

	public static LoginUserList getInstance() {
		if(instance == null){
			instance = new LoginUserList(); 
		}
		 
		return instance;
	}

	public void addLoginUser(AccountInfo accountInfo) {
		if(accountInfo != null){
			accountInfos.put(accountInfo.getSessionId(), accountInfo);
		} 
	}
	
	public void killoutLoginUser(String sessionId){
		AccountInfo accountInfo = accountInfos.remove(sessionId);
		log.info("User logout:" + accountInfo);
	}
	  
	public Collection<AccountInfo> getAccountInfos(){
		List<AccountInfo> l = new ArrayList<AccountInfo>( accountInfos.values());
		 
 		Collections.sort(l, new Comparator<AccountInfo>() { 
			public int compare(AccountInfo o1, AccountInfo o2) {
				long a = 0, b= 0;
				if(o1 != null && o1.getLoginTime() != null){
					a = o1.getLoginTime().getTime();
				}
				if(o2 != null && o2.getLoginTime() != null){
					b = o2.getLoginTime().getTime();
				}
				
 				return (int)(a - b);
			}
		});
		
		return l;
	}
	
}
