package com.softjourn.vending.service;

import com.softjourn.vending.dao.CategoriesRepository;
import com.softjourn.vending.dao.MachineRepository;
import com.softjourn.vending.dao.ProductRepository;
import com.softjourn.vending.dao.PurchaseRepository;
import com.softjourn.vending.dto.DashboardDTO;
import com.softjourn.vending.entity.Categories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MachineRepository machineRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Override
    public DashboardDTO getDashboard() {
        DashboardDTO dashboardDTO = new DashboardDTO();
        dashboardDTO.setProducts(productRepository.count());
        dashboardDTO.setMachines(machineRepository.count());
        dashboardDTO.setCategories(categoriesRepository.count());
        dashboardDTO.setPurchases(purchaseRepository.count());
        return dashboardDTO;
    }
}
