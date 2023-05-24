package com.oasis.onlinestore.repository;

import com.oasis.onlinestore.domain.Order;
import com.oasis.onlinestore.domain.OrderLine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

//@RunWith(SpringRunner.class)
//@DataJpaTest
//public class OrderLineRepositoryTest {
//    @Autowired
//    TestEntityManager testEntityManager;
//    @Autowired
//    OrderLineRepo orderLineRepo;
//    @Test
//    public void findById(){
////        OrderLine orderLine = new OrderLine("iPhone");
////        testEntityManager.persist(orderLine);
//        testEntityManager.flush();
//
//
//
//    }

