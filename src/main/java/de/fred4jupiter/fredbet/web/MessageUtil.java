package de.fred4jupiter.fredbet.web;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Component
public class MessageUtil {

	public void addInfoMsg(RedirectAttributes redirect, String text) {
		addMessage(redirect, "alert-success",text);
	}

	private void addMessage(RedirectAttributes redirect, String cssClass,String text) {
		redirect.addFlashAttribute("globalMessage", new WebMessage(cssClass, text));
	}

	public void addWarnMsg(RedirectAttributes redirect, String text) {
		addMessage(redirect, "alert-success",text);
	}

	public void addErrorMsg(RedirectAttributes redirect, String text) {
		addMessage(redirect, "alert-success",text);
	}

	public static final class WebMessage {
		private String cssClass;

		private String text;

		public WebMessage(String cssClass, String text) {
			super();
			this.cssClass = cssClass;
			this.text = text;
		}

		public String getCssClass() {
			return cssClass;
		}

		public String getText() {
			return text;
		}

	}
}
