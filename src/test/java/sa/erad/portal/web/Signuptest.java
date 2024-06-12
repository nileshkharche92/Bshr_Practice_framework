package sa.erad.portal.web;

import Erad.Base.BaseClass;
import Erad.Pages.LoginPage;

import Erad.config.DefineConstants;
import Erad.helperMethods.JsonUtils;
import org.testng.annotations.Test;


public class Signuptest extends BaseClass {

        @Test(priority=1)
        public void LoginPageMethods(){
            LoginPage loginPageMethods = new LoginPage(driver);
            loginPageMethods.verifybtn();

        }

    }



