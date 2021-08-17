package egovframework.example.sample.board.web;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

//import egovframework.com.cmm.service.EgovFileMngUtil;
//import egovframework.com.cmm.service.Globals;
import egovframework.example.sample.board.service.BoardService;
import egovframework.example.sample.board.service.BoardVO;
import egovframework.rte.fdl.property.EgovPropertyService;
import egovframework.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;

//oauth 관련 import pack

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.MissingAuthorizationException;
import org.springframework.social.connect.Connection;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.User;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import com.github.scribejava.core.model.OAuth2AccessToken;

@Controller
public class BoardController {

	@Resource(name = "propertiesService")
	protected EgovPropertyService propertiesService;

	@Resource(name = "boardService")
	private BoardService boardService;

	@RequestMapping(value = "/mainList.do")
	public String list(@ModelAttribute("boardVO") BoardVO boardVO, ModelMap model) throws Exception {
		/** EgovPropertyService.sample */
		/** resource -> egov -> spring -> properties.xml **/
		boardVO.setPageUnit(propertiesService.getInt("pageUnit"));
		boardVO.setPageSize(propertiesService.getInt("pageSize"));

		/** pageing setting */
		PaginationInfo paginationInfo = new PaginationInfo();
		paginationInfo.setCurrentPageNo(boardVO.getPageIndex());
		paginationInfo.setRecordCountPerPage(boardVO.getPageUnit());
		paginationInfo.setPageSize(boardVO.getPageSize());

		boardVO.setFirstIndex(paginationInfo.getFirstRecordIndex());
		boardVO.setLastIndex(paginationInfo.getLastRecordIndex());
		boardVO.setRecordCountPerPage(paginationInfo.getRecordCountPerPage());

		List<?> list = boardService.selectBoardList(boardVO);
		// resultlist에 list를 넣음
		model.addAttribute("resultList", list);

		int totCnt = boardService.selectBoardListTotCnt(boardVO);
		paginationInfo.setTotalRecordCount(totCnt);
		model.addAttribute("paginationInfo", paginationInfo);

		return "/board/mainList";
	}

	@RequestMapping(value = "/mgmt.do", method = RequestMethod.GET)
	public String mgmt(@ModelAttribute("boardVO") BoardVO boardVO, ModelMap model, HttpServletRequest request)
			throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyymmdd");
		Calendar c1 = Calendar.getInstance();
		String strToday = sdf.format(c1.getTime());
		System.out.println("Today" + strToday);

		// 서버에서 가져오기 (조회 후 설정)
		boardVO = boardService.selectBoard(boardVO);

		// BoardVO boardVO = new BoardVO();
		boardVO.setIndate(strToday);
		boardVO.setWriter(request.getSession().getAttribute("userId").toString());
		boardVO.setWriterNm(request.getSession().getAttribute("userName").toString());

		// 서버에서 가져온 값 맵핑
		model.addAttribute("boardVO", boardVO);

		return "/board/mgmt";
	}

	@RequestMapping(value = "/mgmt.do", method = RequestMethod.POST)
	public String mgmt2(@ModelAttribute("boardVO") BoardVO boardVO, @RequestParam("mode") String mode, ModelMap model)
			throws Exception {
		if ("add".equals(mode)) {
			boardService.insertBoard(boardVO);
		} else if ("modify".equals(mode)) {
			boardService.updateBoard(boardVO);
		} else if ("del".equals(mode)) {
			boardService.deleteBoard(boardVO);
		}

		return "redirect:/mainList.do";
	}

	@RequestMapping(value = "/view.do")
	public String view(@ModelAttribute("boardVO") BoardVO boardVO, ModelMap model) throws Exception {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Calendar c1 = Calendar.getInstance();
		String strToday = sdf.format(c1.getTime());
		System.out.println("Today" + strToday);

		boardService.updateBoardCount(boardVO);

		boardVO = boardService.selectBoard(boardVO);

		model.addAttribute("boardVO", boardVO);
		model.addAttribute("strToday", strToday);

		List<?> list = boardService.selectReplyList(boardVO);
		// resultlist에 list를 넣음
		model.addAttribute("resultList", list);

		return "/board/view";
	}

	@RequestMapping(value = "/reply.do", method = RequestMethod.POST)
	public String reply(@ModelAttribute("boardVO") BoardVO boardVO, ModelMap model) throws Exception {

		boardService.insertReply(boardVO);
		return "redirect:/view.do?idx=" + boardVO.getIdx();
	}

	// 로그인 처리

	@RequestMapping(value = "/login.do")
	public String login(@RequestParam("user_id") String user_id, @RequestParam("password") String password,
			ModelMap model, HttpSession session, HttpServletRequest request) throws Exception {
		String returnUrlGubun = STR_USER;

		if (!"".equals(user_id) && !"".equals(password)) {

			BoardVO boardVO = new BoardVO();
			boardVO.setUserId(user_id);
			boardVO.setPassword(password);
			String user_name = boardService.selectLoginCheck(boardVO);

			if (user_name != null && !"".equals(user_name)) {
				request.getSession().setAttribute("userId", user_id);
				request.getSession().setAttribute("userName", user_name);
			} else {
				request.getSession().setAttribute("userId", "");
				request.getSession().setAttribute("userName", "");
				model.addAttribute("msg", "사용자 정보가 올바르지 않습니다.");
			}
			return "redirect:mainList.do";
		} else {
			// 네이버
			String naverAuthUrl = naverLoginBO.getAuthorizationUrl(session, returnUrlGubun);
			model.addAttribute("naver_url", naverAuthUrl);
			// 페이스북
			OAuth2Operations oauthOperations = connectionFactory.getOAuthOperations();
			oAuth2Parameters.setRedirectUri(propertiesService.getString("site.secure.url")
					+ propertiesService.getString("facebook.api.redirectUri") + returnUrlGubun + ".do");
			String facebook_url = oauthOperations.buildAuthorizeUrl(GrantType.AUTHORIZATION_CODE, oAuth2Parameters);
			model.addAttribute("facebook_url", facebook_url);
			model.addAttribute("kakao_url", kakao.getKakaoUrl(returnUrlGubun));
			return "/board/login";
		}

	}

	@RequestMapping(value = "/logout.do")
	public String logout(ModelMap model, HttpServletRequest request) throws Exception {
		request.getSession().invalidate();
		return "redirect:mainList.do";
	}

	public String selectLoginCheck(BoardVO searchVO) {
		return boardService.selectLoginCheck(searchVO);
	}

	// 회원가입 관련

	@RequestMapping(value = "/showAdd.do")
	public String showMainHT(ModelMap model, HttpServletRequest request) throws Exception {

		return "/board/addMember";
	}

	@RequestMapping(value = "/snsNewAdd.do")
	public String snsAdd(ModelMap model, HttpSession session, HttpServletRequest request) throws Exception {
		String user_id = (String) session.getAttribute("user_id");
		String user_name = (String) session.getAttribute("user_name");
		
		model.addAttribute("user_id", user_id);
		model.addAttribute("user_name", user_name);
		

		request.getSession().invalidate();
		return "/board/addSnsMember";
	}

	@RequestMapping(value = "/snsLogin.do")
	public String snsLogin(ModelMap model, HttpSession session, HttpServletRequest request) throws Exception {
		String user_id = (String) session.getAttribute("user_id");
		model.addAttribute("user_id", user_id);

		request.getSession().invalidate();
		return "/board/snsLogin";
	}

	@RequestMapping(value = "/memberAdd.do", method = { RequestMethod.POST })
	public String addMainHT(@RequestParam("user_id") String user_id, @RequestParam("password") String password,
			@RequestParam("user_name") String user_name, ModelMap model, HttpServletRequest request) throws Exception {

		BoardVO boardVO = new BoardVO();
		boardVO.setUserId(user_id);
		boardVO.setPassword(password);
		boardVO.setUserName(user_name);

		try {
			// checker = 0 : 중복되는 아이디 없음
			// checker != 0 : 중복되는 아이디 있음
			int checker = boardService.searchIDcheck(boardVO);
			System.out.println("접근 ID : " + user_id);
			System.out.println("중복 아이디 수 : " + checker);

			if (checker != 0) {
				model.addAttribute("add", "아이디가 중복되었습니다.");
			} else {
				model.addAttribute("add", "회원가입에 성공하였습니다. 메인에서 로그인해주세요.");
				boardService.addMember(boardVO);
			}

		} catch (Exception e) {
			System.out.println("except");
			model.addAttribute("add", "아이디를 입력해주세요.");
		}

		return "/board/message";
	}

	// oauth 관련

	private final static String STR_USER = "User";

	@Autowired
	private NaverLoginBO naverLoginBO;

	private String apiResult = null;

	@Autowired
	private KakaoLoginApi kakao;

	@Autowired
	private FacebookConnectionFactory connectionFactory;

	@Autowired
	private OAuth2Parameters oAuth2Parameters;

//	public String testLogin(String user_id, HttpSession session, ModelMap model) {
//		System.out.println("login info : " + user_id);
//		BoardVO boardVO = new BoardVO();
//		boardVO.setUserId(user_id);
//		
//		try {
//			int checker = boardService.searchIDcheck(boardVO);
//			System.out.println("tt접근 ID : " + user_id);
//			System.out.println("tt중복 아이디 수 : " + checker);
//			
//			if (checker != 0) {
//				// 중복되는 아이디 있음, 해당 아이디의 pw 입력 성공 시 연동 // 실패시 아이디 새로 받아 가입
////				session.setAttribute("user_id", user_id);
//				snsLogin(model, user_id);
//				return "/board/snsLogin";
//
//			}else {
//				return "redirect:mainList.do";
//
//				// 중복되는 아이디 없음. 해당 아이디 그대로 가입하고 비밀번호 및 닉네임 새로 받음
//			}
//
//		}catch (Exception e){
//			System.out.println("except");
//			model.addAttribute("add", "에러가 발생했습니다.");
//			return "redirect:mainList.do";
//
//		}
//	}

	// 카카오톡 로그인
	@RequestMapping(value = "/kakaoCallbackUser.do", method = { RequestMethod.GET, RequestMethod.POST })
	public String kakaoCallbackSports(ModelMap model, @RequestParam("code") String code, HttpSession session,
			HttpServletResponse response, HttpServletRequest request) throws Exception {
		return kakaoCallback(model, code, session, response, request);
	}

	// 카카오톡 로그인
	public String kakaoCallback(ModelMap model, String code, HttpSession session, HttpServletResponse response,
			HttpServletRequest request) throws Exception {
		String access_Token = kakao.getAccessToken(code, STR_USER);
		HashMap<String, Object> userInfo = kakao.getUserInfo(access_Token);
		HashMap<String, Object> map = new HashMap<>();
		BoardVO boardVO = new BoardVO();
		// requset를 통해 받아온 ID값을 컨트롤러로 불러옴
		String user_id = (String) userInfo.get("userid");
		System.out.println("nickname " + user_id);
		boardVO.setUserId(user_id);

		try {
			int checker = boardService.searchIDcheck(boardVO);
			System.out.println("접근 ID : " + user_id);
			System.out.println("중복 아이디 수 : " + checker);

			if (checker != 0) {
				// 중복되는 아이디 있음, 해당 아이디의 pw 입력 성공 시 연동 // 실패시 아이디 새로 받아 가입
				session.setAttribute("user_id", user_id);
				return "redirect:snsLogin.do";

			} else {
				// 중복되는 아이디 없음. 해당 아이디 그대로 가입하고 비밀번호 및 닉네임 새로 받음
				session.setAttribute("user_id", user_id);
				return "redirect:snsNewAdd.do";
			}

		} catch (Exception e) {
			System.out.println("except");
			model.addAttribute("add", "에러가 발생했습니다.");

		}
		return "redirect:mainList.do";
	}

	// 네이버 로그인 후 메인 페이지로 콜백
	@RequestMapping(value = "/naverCallbackUser.do", method = { RequestMethod.GET, RequestMethod.POST })
	public String naverCallbackUser(ModelMap model, @RequestParam String code, @RequestParam String state,
			HttpSession session, HttpServletResponse response, HttpServletRequest request) throws Exception {
		String cmpnyNm = STR_USER;
		return naverCallback(model, code, state, cmpnyNm, session, response, request);
	}

	private String naverCallback(ModelMap model, String code, String state, String cmpnyNm, HttpSession session,
			HttpServletResponse response, HttpServletRequest request) throws Exception {
		OAuth2AccessToken oauthToken;
		oauthToken = naverLoginBO.getAccessToken(session, code, state, cmpnyNm);
		apiResult = naverLoginBO.getUserProfile(oauthToken, cmpnyNm); // String형식의 json데이터
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(apiResult);
		JSONObject jsonObj = (JSONObject) obj;
		String resultcode = (String) jsonObj.get("resultcode");

		JSONObject response_obj = (JSONObject) jsonObj.get("response");
		String user_id = (String) response_obj.get("user_id");
		

		if (!resultcode.equals("00")) {
			System.out.println("네이버 인증에 실패하였습니다. 다시 시도하여 주십시오.");
		}

		// [TODO] DB 조회하여 카톡 아이디 연계등록 여부 확인
//	        등록 확인시 로그인 처리 및 목록 화면 이동
//	        등록된 데이터가 없을 경우 회원가입으로 안내.
//		testLogin(id, session, model);

		return "redirect:mainList.do";
	}

	@RequestMapping(value = "/facebookCallbackUser.do", method = { RequestMethod.GET, RequestMethod.POST })
	public String facebookCallbackUser(@RequestParam String code, HttpServletRequest request, HttpSession session,
			HttpServletResponse response, ModelMap model) throws Exception {
		String cmpnyNm = STR_USER;
		return facebookCallback(code, cmpnyNm, request, session, response, model);

	}

	// 페이스북 로그인 후 메인 페이지로 콜백
	public String facebookCallback(String code, String cmpnyNm, HttpServletRequest request, HttpSession session,
			HttpServletResponse response, ModelMap model) throws Exception {
		try {
			String redirectUri = propertiesService.getString("site.secure.url")
					+ propertiesService.getString("facebook.api.redirectUri") + cmpnyNm + ".do";

			OAuth2Operations oauthOperations = connectionFactory.getOAuthOperations();
			AccessGrant accessGrant = oauthOperations.exchangeForAccess(code, redirectUri, null);
			String accessToken = accessGrant.getAccessToken();
			System.out.println("AccessToken: " + accessToken);
			Long expireTime = accessGrant.getExpireTime();

			if (expireTime != null && expireTime < System.currentTimeMillis()) {
				accessToken = accessGrant.getRefreshToken();
				System.out.println("accessToken is expired. refresh token = {}" + accessToken);
			};

			Connection<Facebook> connection = connectionFactory.createConnection(accessGrant);
			Facebook facebook = connection == null ? new FacebookTemplate(accessToken) : connection.getApi();
			facebook.userOperations();

			try {
				String[] fields = { "id", "email", "name" };
				User userProfile = facebook.fetchObject("me", User.class, fields);
				System.out.println("유저 id : " + userProfile.getId());
				String id = userProfile.getId();
				System.out.println("Facebook id : " + id);
				String email = userProfile.getEmail();
				System.out.println("Facebook email : " + email);
				String name = userProfile.getName();
				System.out.println("Facebook name : " + name);
				BoardVO boardVO = new BoardVO();
				
				String user_id = (String) userProfile.getEmail();
				String user_name = (String) userProfile.getName();
				System.out.println("Email " + user_id);
				boardVO.setUserId(user_id);
				boardVO.setUserName(user_name);
				int checker = boardService.searchIDcheck(boardVO);
				System.out.println("접근 ID : " + user_id);
				System.out.println("중복 아이디 수 : " + checker);

				if (checker != 0) {
					// 중복되는 아이디 있음, 해당 아이디의 pw 입력 성공 시 연동 // 실패시 아이디 새로 받아 가입
					System.out.println("중복 ID 존재");
					session.setAttribute("user_id", user_id);
					session.setAttribute("user_name", user_name);
					return "redirect:snsLogin.do";

				} else {
					// 중복되는 아이디 없음. 해당 아이디 그대로 가입하고 비밀번호 및 닉네임 새로 받음
					System.out.println("중복 ID 없음");
					session.setAttribute("user_id", user_id);
					session.setAttribute("user_name", user_name);
					return "redirect:snsNewAdd.do";
				}

				// [TODO] DB 조회하여 카톡 아이디 연계등록 여부 확인 //등록 확인시 로그인 처리 및 목록 화면 이동 // 등록된 데이터가 없을 경우
				// 회원가입으로 안내.
//	                testLogin(id, session, model);
			} catch (MissingAuthorizationException e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "redirect:mainList.do";
	}

}
