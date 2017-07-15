package de.fred4jupiter.fredbet.web.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.service.admin.CacheAdministrationService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;

@Controller
@RequestMapping("/cache")
@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_ADMINISTRATION + "')")
public class CacheController {

	@Autowired
	private CacheAdministrationService cacheAdministrationService;

	@Autowired
	private WebMessageUtil messageUtil;
	
	@RequestMapping(value = "/show", method = RequestMethod.GET)
	public ModelAndView showCachePage() {
		return new ModelAndView("admin/cache");
	}
	
	@RequestMapping(path = "/clearCache", method = RequestMethod.GET)
	public ModelAndView clearCache(ModelMap modelMap) {
		final ModelAndView modelAndView = new ModelAndView("admin/cache");

		this.cacheAdministrationService.clearCaches();
		
		messageUtil.addInfoMsg(modelMap, "administration.msg.info.cacheCleared");
		return modelAndView;
	}

}
