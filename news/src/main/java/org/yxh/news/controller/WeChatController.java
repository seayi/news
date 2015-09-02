package org.yxh.news.controller;

import java.io.IOException;
import java.text.ParseException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yxh.news.wechat.CheckModel;
import org.yxh.news.wechat.TokenService;

@Controller
@RequestMapping("/")
public class WeChatController {
	private static final Logger logger = Logger
			.getLogger(WeChatController.class);
	@Autowired
	private TokenService tokenService;

//	@RequestMapping(value = "/", method = RequestMethod.POST, produces = "application/xml;charset=UTF-8")
//	// @RequestMapping(value="/wechat",method= RequestMethod.GET)
//	public String wechat(HttpServletRequest request,
//			HttpServletResponse response) {
//		try {
//			request.setCharacterEncoding("UTF-8");
//			response.setCharacterEncoding("UTF-8");
//		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
//		}
//		return "wechat";
//	}
	
	/**
     * 开发者模式token校验
     *
     * @param wxAccount 开发者url后缀
     * @param response
     * @param tokenModel
     * @throws ParseException
     * @throws IOException
     */
    @RequestMapping(value = "/check/{wxToken}", method = RequestMethod.GET, produces = "text/plain")
    public @ResponseBody String validate(@PathVariable("wxToken")String wxToken,CheckModel tokenModel) throws ParseException, IOException {
        return tokenService.validate(wxToken,tokenModel);
    }
}
