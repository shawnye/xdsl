package unicom.web;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class XdslSessionListener implements HttpSessionListener {

	public void sessionCreated(HttpSessionEvent arg0) {
 
	}

	public void sessionDestroyed(HttpSessionEvent e) {
		LoginUserList.getInstance().killoutLoginUser(e.getSession().getId()); 
	}

}
