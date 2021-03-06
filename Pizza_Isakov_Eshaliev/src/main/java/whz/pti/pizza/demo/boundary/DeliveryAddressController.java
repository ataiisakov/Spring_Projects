package whz.pti.pizza.demo.boundary;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import whz.pti.pizza.demo.domain.DeliveryAddress;
import whz.pti.pizza.demo.domain.repositories.CustomerRepository;
import whz.pti.pizza.demo.domain.repositories.DeliveryAddressRepository;
import whz.pti.pizza.demo.security.boundary.CurrentUserControllerAdvice;
import whz.pti.pizza.demo.security.domain.Customer;
import whz.pti.pizza.demo.security.domain.DeliveryAddressForm;
import whz.pti.pizza.demo.security.domain.User;
import whz.pti.pizza.demo.security.service.validator.DeliveryAddressCreateValidator;

import javax.validation.Valid;

@Controller
@Slf4j
public class DeliveryAddressController {

    @Autowired
    DeliveryAddressRepository deliveryAddressRepository;
    @Autowired
    CurrentUserControllerAdvice currentUserControllerAdvice;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    DeliveryAddressCreateValidator deliveryAddressCreateValidator;


    @GetMapping("/deliveryAddress")
    public String DeliveryAddressPage(Model model){
        model.addAttribute("listAllDeliveryAddresses", deliveryAddressRepository.findAll());
        return "deliveryAddress";
    }

    @InitBinder("daForm")
    public void initBinder(WebDataBinder binder){
        binder.addValidators(deliveryAddressCreateValidator);
    }

    @GetMapping("/newAddress")
    public String NewAddressPage(){
        return "newAddress";
    }

    @PostMapping("/newAddress")
    public String processRegistration(@Valid @ModelAttribute("daForm") DeliveryAddressForm form,
                                      BindingResult bindingResult, Authentication auth,
                                      Model model){
        if (bindingResult.hasErrors()){
            model.addAttribute("error",bindingResult.getGlobalError().getDefaultMessage());
            return "newAddress";
        }

        User user = currentUserControllerAdvice
                .getCurrentUser(auth)
                .getUser();
        Customer customer = customerRepository
                .getByLoginName(user.getLoginName());
        if (customer == null){
            return "redirect:/home";
        }

        DeliveryAddress da = new DeliveryAddress(form.getStreet(), form.getHauseNumber(),
                form.getTown(),form.getPostalCode());
        da.addCustomer(customer);
        customer.addDeliveryAddress(da);

        deliveryAddressRepository.save(da);

        log.info("Add address "+da);
        log.info("Costumer "+customer);
        return "redirect:/deliveryAddress";
    }
}