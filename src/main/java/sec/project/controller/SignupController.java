package sec.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sec.project.config.CustomUserDetailsService;
import sec.project.config.SecurityConfiguration;
import sec.project.domain.Signup;
import sec.project.repository.SignupRepository;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
public class SignupController {

    @Autowired
    private SignupRepository signupRepository;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    HttpSession session;

    @RequestMapping("*")
    public String defaultMapping() {
        return "redirect:/form";
    }

    @RequestMapping(value = "/form", method = RequestMethod.GET)
    public String loadForm() {
        return "form";
    }

    @RequestMapping(value = "/form", method = RequestMethod.POST)
    public String submitForm(@RequestParam String name, @RequestParam String address,
                             @RequestParam String login, @RequestParam String password,
                             Model model) {
        Signup signup = new Signup(name, address, login, password);
        signupRepository.save(signup);
        if (login != null) {
            userDetailsService.addUser(login, password);
            session.setAttribute("signup", signup.getId());
        }
        model.addAttribute("participant", signup);
        return "done";
    }

    @RequestMapping(value = "/remove", method = RequestMethod.GET)
    public String remove(@RequestParam Long id) {
        if (id != null) {
            Signup remove = signupRepository.findById(id);
            signupRepository.delete(remove);
        }
        return "admin";
    }

    @RequestMapping(value = "/removeAll", method = RequestMethod.GET)
    public String removeAll() {
        // Oh boy this is bad
        signupRepository.deleteAll();
        return "admin";
    }

    @RequestMapping(value = "/removeParticipant", method = RequestMethod.GET)
    public String removeParticipant(@RequestParam Long id) {
        if (id != null) {
            Signup remove = signupRepository.findById(id);
            signupRepository.delete(remove);
        }
        return "form";
    }

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String manageForm(Model model) {
        model.addAttribute("participants", signupRepository.findAll());
        return "admin";
    }

    @RequestMapping(value = "/manage", method = RequestMethod.GET)
    public String manageForm(@RequestParam(value = "id", required = false) Long id, Model model, RedirectAttributes redirectAttributes) {
        if (id == null){
            id = (Long)session.getAttribute("signup");
            if (id != null) {
                redirectAttributes.addAttribute("id", id);
                return "redirect:/manage";
            }
            return "redirect:/form";
        }
        model.addAttribute("participants", signupRepository.findById(id));
        return "manage";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout() {
        //do nothing
        return "redirect:/form";
    }


}
