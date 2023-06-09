package bdbt_project.SpringApplication;

import bdbt_project.SpringApplication.dbDAO.*;
import bdbt_project.SpringApplication.dbtables.*;
import bdbt_project.SpringApplication.dto.KlientPassword;

import bdbt_project.SpringApplication.files.FilesStorageService;
import bdbt_project.SpringApplication.files.FilesStorageServiceImpl;
import bdbt_project.SpringApplication.filters.*;
import bdbt_project.SpringApplication.utility.RandomUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Controller
public class AppController implements WebMvcConfigurer {

    @Autowired
    private final AdresDAO adresDAO = new AdresDAO(new JdbcTemplate());

    @Autowired
    private final UmowaDAO umowaDAO = new UmowaDAO(new JdbcTemplate());

    @Autowired
    private final KlientDAO klientDAO = new KlientDAO(new JdbcTemplate());

    @Autowired
    private final ZwierzeDAO zwierzeDAO = new ZwierzeDAO(new JdbcTemplate());

    @Autowired
    private final PracownikDAO pracownikDAO = new PracownikDAO(new JdbcTemplate());

    @Autowired
    private final SchroniskoDAO schroniskoDAO = new SchroniskoDAO(new JdbcTemplate());

    @Autowired
    private final WynagrodzenieDAO wynagrodzenieDAO = new WynagrodzenieDAO(new JdbcTemplate());

    @Autowired
    private final FilesStorageService storageService = new FilesStorageServiceImpl();

    private final KlientPasswordDAO klientPasswordDAO = new KlientPasswordDAO();

    private Integer currentAnimal = null;
    private GatunekFilter gatunekFilter = new GatunekFilter();
    private Umowa currUmowaParams = null;
    private Accept accept = new Accept();
    private Decline decline = new Decline();
    private UmowaFilter umowaFilter = new UmowaFilter();
    private PracownikFilter pracownikFilter = new PracownikFilter();

    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/index").setViewName("index");
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/main").setViewName("main");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/main_admin").setViewName("admin/main_admin");
        registry.addViewController("/main_user").setViewName("user/main_user");
        registry.addViewController("/register").setViewName("register");
        registry.addViewController("/navbar").setViewName("navbar");
        registry.addViewController("/zwierzeta").setViewName("zwierzeta");
        registry.addViewController("/umowy.html").setViewName("user/umowy");
        registry.addViewController("/podpisz").setViewName("user/podpisz");
        registry.addViewController("/profil_zwierzecia").setViewName("profil_zwierzecia");
        registry.addViewController("/lokacje").setViewName("lokacje");
        registry.addViewController("/main_employee").setViewName("employee/main_employee");
        registry.addViewController("/emp_umowy").setViewName("employee/emp_umowy");
        registry.addViewController("/emp_umowy_all").setViewName("employee/emp_umowy_all");
        registry.addViewController("/umowa").setViewName("employee/umowa");
        registry.addViewController("/employees").setViewName("admin/employees");
        registry.addViewController("/add_employee").setViewName("admin/add_employee");
        registry.addViewController("/add_animal").setViewName("employee/add_animal");
        registry.addViewController("/all_animals").setViewName("all_animals");
        registry.addViewController("/user_profile").setViewName("user/user_profile");
        registry.addViewController("/edit_profile").setViewName("user/edit_profile");
    }

    @Controller
    public class DashboardController {
        @RequestMapping("/main")
        public String defaultAfterLogin(HttpServletRequest request) {
            var userRole = request.getRemoteUser();
            var userRolesRedirect = Application.getUserRedirect();
            return userRolesRedirect.getOrDefault(userRole, "redirect:/index");
        }
    }

    @RequestMapping("/adresy")
    public String showAdresyPage(Model model) {
        List<Adres> listAdres = adresDAO.list();
        // System.out.println(listAdres);
        model.addAttribute("listAdres", listAdres);
        return "adresy";
    }

    @RequestMapping(value={"selectAnimal"}, method=RequestMethod.POST)
    public String getCurrentAnimal(@ModelAttribute("selected") String selected) {
        currentAnimal = Integer.parseInt(selected);
        System.out.println("animal="+currentAnimal);
        return "redirect:/user/podpisz";
    }

    @RequestMapping(value={"selectAnimal1"}, method=RequestMethod.POST)
    public String getCurrentAnimal_1(@ModelAttribute("selected1") String selected1) {
        currentAnimal = Integer.parseInt(selected1);
        System.out.println("animal="+currentAnimal);
        return "redirect:/profil_zwierzecia";
    }

    @RequestMapping("/user/umowy")
    public void showUmowy(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        var user = klientDAO.getByEmail(email);
        var umowy = umowaDAO.listWhereId(user.getNr_klienta());
        var zwierzeta = zwierzeDAO.getZwierzetaOfIds(
                (ArrayList<Integer>)umowaDAO.listZwierzetaIdOfCurrentKlient(umowy));
        var pracownicy = pracownikDAO.getByUmowy(umowy);
        System.out.println(user);
        System.out.println(umowy);
        System.out.println(zwierzeta);
        System.out.println(pracownicy);
        var umowyInfo = umowaDAO.listUmowaInfo(umowy, pracownicy, zwierzeta);
        model.addAttribute("umowaInfo", umowyInfo);
    }

    @RequestMapping(value={"/main_admin"})
    public String showAdminPage(Model model) {
        return "admin/main_admin";
    }

    @RequestMapping(value={"/register"})
    public String showRegisterForm(Model model) {
        var klient = new Klient();
        var klientHaslo = new KlientPassword();
        var adres = new Adres();
        model.addAttribute("klientDAO", klient);
        model.addAttribute("klientHaslo", klientHaslo);
        model.addAttribute("adresDAO", adres);
        return "register";
    }

    @RequestMapping(value="/save_klient_data", method=RequestMethod.POST)
    public String saveKlientData(@ModelAttribute("klientDAO") Klient klient,
                                 @ModelAttribute("adresDAO") Adres adres,
                                 @ModelAttribute("klientHaslo") KlientPassword kh) throws Exception {
        adresDAO.save(adres);
        var nr_adresu = adresDAO.getHighestIdAddress().getNr_adresu();
        klient.setNr_adresu(nr_adresu);
        var email = klient.getEmail();
        kh.setEmail(email);
        klientDAO.save(klient);
        klientPasswordDAO.save(kh);
        return "redirect:/main_user";
    }

    @RequestMapping(value={"/getFilter"}, method=RequestMethod.POST)
    public String changeFilter(@ModelAttribute("gatunekFilter") GatunekFilter gatunekFilter) {
        this.gatunekFilter = gatunekFilter;
        return "redirect:/zwierzeta";
    }

    @RequestMapping("/zwierzeta")
    public String showZwierzeta(Model model) {
        var params = gatunekFilter.getSelected();

        var lista = zwierzeDAO.listWhereGatunek(params);
        var lista2 = umowaDAO.getCzyWSchronisku(lista);
        var listZwierzeta = zwierzeDAO.getAvailableOnly(lista2);
        model.addAttribute("gatunekFilter", gatunekFilter);
        model.addAttribute("listZwierzeta", listZwierzeta);
        return "/zwierzeta";
    }

    @RequestMapping("/user/podpisz")
    public void showPodpiszUmowe(Model model) {
        var animalIds = new ArrayList<Integer>();
        animalIds.add(currentAnimal);
        var zwierze = zwierzeDAO.getZwierzetaOfIds(animalIds).get(0);
        var dostepniPracownicy = pracownikDAO.getPracownikByStanowisko(Pracownik.ZWYKLY_PRACOWNIK);
        var randomPracownik = RandomUtility.choice(dostepniPracownicy);
        model.addAttribute("zwierze", zwierze);
        model.addAttribute("pracownik", randomPracownik);
        model.addAttribute("data_rozpoczecia", new Umowa());
        // System.out.println(zwierze);
        // System.out.println(randomPracownik);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        var currentKlient = klientDAO.getByEmail(email);
        currUmowaParams = new Umowa(0,'P',"","",
                zwierze.getNr_zwerzecia(), currentKlient.getNr_klienta(),
                randomPracownik.getNr_pracownika());
    }

    @RequestMapping(value={"/getDate"}, method=RequestMethod.POST)
    public String getDateForUmowa(@ModelAttribute("data_rozpoczecia") Umowa umowa) {
        currUmowaParams.setData_podpisu(umowa.getData_podpisu());
        System.out.println(currUmowaParams + " " + umowa.getData_podpisu());
        umowaDAO.save(currUmowaParams);
        return "user/main_user";
    }

    @RequestMapping("/profil_zwierzecia")
    public void showProfilZwierzecia(Model model) {
        var zwierze = zwierzeDAO.getZwierzeById(currentAnimal);
        model.addAttribute("zwierze", zwierze);
    }

    @RequestMapping("/lokacje")
    public void showLokacje(Model model) {
        var lokacje = schroniskoDAO.list();
        var lokacjeIds = schroniskoDAO.listIds(lokacje);
        var adresy = adresDAO.listAdresyForSchroniskaId(lokacjeIds);
        model.addAttribute("listSchroniska", lokacje);
        model.addAttribute("listAdresy", adresy);
    }


    @RequestMapping(value={"/getAccept"}, method=RequestMethod.POST)
    public String mapAccept(@ModelAttribute("accept") Accept a) {
        this.decline.setId("0");
        this.accept = a;
//        System.out.println("accept="+this.accept);
//        System.out.println("decline="+this.decline);
        return "redirect:/employee/emp_umowy";
    }

    @RequestMapping(value={"/getDecline"}, method=RequestMethod.POST)
    public String mapDecline(@ModelAttribute("decline") Decline d) {
        this.accept.setId("0");
        this.decline = d;
//        System.out.println("accept="+this.accept);
//        System.out.println("decline="+this.decline);
        return "redirect:/employee/emp_umowy";
    }

    @RequestMapping("/employee/emp_umowy")
    public void showEmpUmowy(Model model) {
        model.addAttribute("accept", accept);
        model.addAttribute("decline", decline);
        if(this.decline.getId() != null) {
            umowaDAO.delete(Integer.parseInt(this.decline.getId()));
        }
        if(this.accept.getId() != null) {
            umowaDAO.updateRodzaj(Integer.parseInt(this.accept.getId()), 'T');
        }
        var umowy = umowaDAO.list();
        var zwierzeta = zwierzeDAO.getZwierzetaByUmowy(umowy);
        var klienci = klientDAO.getKlienciByUmowy(umowy);
        var umowyInfo = umowaDAO.listUmowaKlientNotSigned(umowy, zwierzeta, klienci);
        model.addAttribute("umowyList", umowyInfo);
    }

    @RequestMapping("/employee/emp_umowy_all")
    public void showEmpUmowyAll(Model model) {
        model.addAttribute("select", umowaFilter);
        var umowy = umowaDAO.list();
        var zwierzeta = zwierzeDAO.getZwierzetaByUmowy(umowy);
        var klienci = klientDAO.getKlienciByUmowy(umowy);
        var umowyInfo = umowaDAO.listUmowaKlient(umowy, zwierzeta, klienci);
        model.addAttribute("umowyList", umowyInfo);
    }

    @RequestMapping(value={"/getUmowa"}, method=RequestMethod.POST)
    public String getUmowa(@ModelAttribute("select") UmowaFilter umowaFilter) {
        this.umowaFilter = umowaFilter;
        return "redirect:/employee/umowa";
    }

    @RequestMapping("/employee/umowa")
    public void showEmpUmowa(Model model) {
        var umowa = umowaDAO.getUmowaById(Integer.parseInt(umowaFilter.getId()));
        var nr_zwierzecia = umowa.getNr_zwerzecia();
        var nr_klienta = umowa.getNr_klienta();
        var zwierze = zwierzeDAO.getZwierzeById(nr_zwierzecia);
        var klient = klientDAO.getByNrKlienta(nr_klienta);
        model.addAttribute("umowa", umowa);
        model.addAttribute("zwierze", zwierze);
        model.addAttribute("klient", klient);
    }

    @RequestMapping("/admin/employees")
    public void showPracownicy(Model model) {
        model.addAttribute("fired", pracownikFilter);
        if(this.pracownikFilter.getSelected().size() != 0) {
            var nr_pracownika = Integer.parseInt(this.pracownikFilter.getSelected().get(0));
            wynagrodzenieDAO.deleteByIdPracownik(nr_pracownika);
            pracownikDAO.delete(nr_pracownika);
        }
        var pracownicyList = pracownikDAO.list();
        model.addAttribute("pracownicyList", pracownicyList);
    }

    @RequestMapping(value={"/fire"}, method=RequestMethod.POST)
    public String getFired(@ModelAttribute("fired") PracownikFilter pracownikFilter) {
        this.pracownikFilter = pracownikFilter;
        return "redirect:/admin/employees";
    }

    @RequestMapping("/admin/add_employee")
    public void showAddEmployee(Model model) {
        var pracownik = new Pracownik();
        var adres = new Adres();
        model.addAttribute("adres", adres);
        model.addAttribute("pracownik", pracownik);
    }

    @RequestMapping(value="/savePracownik", method=RequestMethod.POST)
    public String savePracownik(@ModelAttribute("pracownik") Pracownik pracownik,
                                 @ModelAttribute("adres") Adres adres) {
        adresDAO.save(adres);
        var nr_adresu = adresDAO.getHighestIdAddress().getNr_adresu();
        pracownik.setNr_adresu(nr_adresu);
        pracownik.setSposob_wyplaty('G');
        pracownik.setNr_schroniska(1);
        pracownikDAO.save(pracownik);
        return "redirect:/admin/employees";
    }

    @RequestMapping("/employee/add_animal")
    public void showAddAnimal(Model model) {
        var zwierze = new Zwierze();
        model.addAttribute("zwierze", zwierze);
    }

    @RequestMapping(value={"/saveZwierze"}, method=RequestMethod.POST)
    public String saveZwierze(@ModelAttribute("zwierze") Zwierze zwierze) throws IOException {
        zwierze.setNr_schroniska(1);
        zwierze.setNr_zwerzecia(0);
        System.out.println(zwierze);
        zwierzeDAO.save(zwierze);
        var id = zwierzeDAO.getMostRecentZwierzeId().getNr_zwerzecia();
        var file = zwierze.getImage();
        System.out.println(zwierze);
        try {
            storageService.save(file, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/main";
    }

    @RequestMapping("/all_animals")
    public void showAllAnimals(Model model) {
        var params = gatunekFilter.getSelected();
        var lista = zwierzeDAO.listWhereGatunek(params);
        var listZwierzeta = umowaDAO.getCzyWSchronisku(lista);
        model.addAttribute("gatunekFilter2", gatunekFilter);
        model.addAttribute("listZwierzeta1", listZwierzeta);
    }

    @RequestMapping(value={"/getFilter2"}, method=RequestMethod.POST)
    public String changeFilter2(@ModelAttribute("gatunekFilter2") GatunekFilter gatunekFilter) {
        this.gatunekFilter = gatunekFilter;
        return "redirect:/all_animals";
    }

    @RequestMapping("/user/user_profile")
    public void showUserProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        var klient = klientDAO.getByEmail(email);
        var adres = adresDAO.getNrAdresu(klient.getNr_adresu());
        model.addAttribute("klient", klient);
        model.addAttribute("adres", adres);
    }

    @RequestMapping("/user/edit_profile")
    public void showEditProfileForm(Model model) {
        var klient = new Klient();
        var klientHaslo = new KlientPassword();
        var adres = new Adres();
        model.addAttribute("klientDAO", klient);
        model.addAttribute("klientHaslo", klientHaslo);
        model.addAttribute("adresDAO", adres);
    }

    @RequestMapping(value="/save_klient_data2", method=RequestMethod.POST)
    public String saveKlientData2(@ModelAttribute("klientDAO") Klient klient,
                                 @ModelAttribute("adresDAO") Adres adres,
                                 @ModelAttribute("klientHaslo") KlientPassword kh) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        var currentKlient = klientDAO.getByEmail(email);
        var currentAdres = adresDAO.getNrAdresu(currentKlient.getNr_adresu());
        currentAdres = adresDAO.updateOnlyNotNull(currentAdres, adres);
        currentKlient = klientDAO.updateOnlyNotNull(currentKlient, klient);
        kh.setEmail(email);
        klientDAO.update(currentKlient);
        if(kh.getPassword() != null && !kh.getPassword().equals("")) {
            klientPasswordDAO.save(kh);
        }
        adresDAO.update(currentAdres);
        return "redirect:/user/user_profile";
    }
}
