package com.nc.service.impl;

import com.nc.model.Category;
import com.nc.model.Characteristic;
import com.nc.model.Hardware;
import com.nc.repository.CategoryRepository;
import com.nc.service.CategoryService;
import com.nc.service.HardwareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository dao;
    private final HardwareService hardwareService;
    private final CharacteristicServiceImpl characteristicService;

    @Override
    public List<Category> findAll() {
        log.info("Taking data from a database (All categories)");
        return dao.findAll();
    }

    @Override
    public Category findById(long idCategory) {
        log.info("Taking data from the database (Category by ID)");
        return dao.findById(idCategory);
    }

    @Override
    public void save(Category category) {
        log.info("Writing category data to the database\n" + category.toString());
        dao.save(category);
    }

    @Override
    public void update(Category category) {
        log.info("Updating category data in the database\n" + category.toString());
        save(category);
    }

    @Override
    public void delete(long id) {
        List<Hardware> hardwares = hardwareService.findByCategory(id);
        hardwares.forEach(hardware -> hardware.setCategory(null));
        hardwareService.saveAll(hardwares);
        List<Characteristic> characteristics = characteristicService.findAllByCategoryId(id);
        characteristics.forEach(characteristic -> characteristic.setCategory(null));
        log.info("Delete category from the database");
        dao.deleteById(id);
    }
}
