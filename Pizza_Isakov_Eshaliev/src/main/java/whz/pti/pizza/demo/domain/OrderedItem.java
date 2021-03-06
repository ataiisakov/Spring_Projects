package whz.pti.pizza.demo.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import whz.pti.pizza.demo.common.BaseEntity;
import whz.pti.pizza.demo.security.domain.Customer;

import javax.persistence.*;


@Entity
@Getter
@Setter
@NoArgsConstructor

public class OrderedItem extends BaseEntity<Long> {

    @ManyToOne
    private Pizza pizza;

    private int quantity;

    @ManyToOne
    private Customer customer;
    @Enumerated(value = EnumType.STRING)
    private PizzaSize pizzaSize;
}
