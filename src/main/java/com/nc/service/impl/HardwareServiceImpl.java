package com.nc.service.impl;

import com.nc.model.Characteristic;
import com.nc.model.Hardware;
import com.nc.model.HardwareForm;
import com.nc.repository.HardwareRepository;
import com.nc.service.HardwareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HardwareServiceImpl implements HardwareService {
    private final HardwareRepository dao;

    @Override
    public List<Hardware> findAllWhereCountNotNull() {
        log.info("Taking data from the database (All components for which CountHardware! = 0)");
        return deleteHardwareWhereCountEqualsNull(dao.findAll());
    }

    @Override
    public List<Hardware> findAll() {
        log.info("Taking data from the database (All components)");
        return dao.findAll();
    }

    @Override
    public Hardware findById(long id) {
        Hardware hardware = dao.findById(id);
        log.info("Taking data from the database (Component by ID)\n" + hardware.toString());
        return dao.findById(id);
    }

    @Override
    public List<Hardware> findByCategory(long id) {
        log.info("Taking data from the database (Accessories by category ID)");
        return deleteHardwareWhereCountEqualsNull(dao.findByCategory_Id(id));
    }

    @Override
    public void update(Hardware hardware) {
        log.info("Updating component data in the database\n" + hardware.toString());
        save(hardware);
    }

    @Override
    public void save(Hardware hardware) {
        log.info("Writing component data to the database\n" + hardware.toString());
        dao.save(hardware);
    }

    @Override
    public void addNewHardware(HardwareForm hardwareForm) {
        byte[] image = null;
        try {
            image = hardwareForm.getImage().getBytes();
        } catch (Exception e) {
            log.error("An error occurred in " + hardwareForm.getName() + " when converting images from byte to blob :\n" + e);
        }
        Hardware hardware = new Hardware();
        hardware.setName(hardwareForm.getName());
        hardware.setCategory(hardwareForm.getCategory());
        hardware.setDescription(hardwareForm.getDescription());
        hardware.setCharacteristics(hardwareForm.getCharacteristics());
        hardware.setPrice(Double.parseDouble(hardwareForm.getPrice()));
        hardware.setTotalCount(hardwareForm.getTotalCount());
        hardware.setImage(image);
        save(hardware);
    }

    @Override
    public void editHardware(HardwareForm hardwareForm) {
        Hardware oldHardware = dao.findById(hardwareForm.getId());
        Hardware newHardware = new Hardware();
        newHardware.setId(hardwareForm.getId());
        try {
            if (hardwareForm.getImage().getSize() != 0) {
                newHardware.setImage(hardwareForm.getImage().getBytes());
            } else {
                newHardware.setImage(oldHardware.getImage());
            }
        } catch (Exception e) {
            log.error("An error occurred in " + hardwareForm.getName() + " when converting images from byte to blob :\n" + e);
        }
        newHardware.setName(hardwareForm.getName());
        newHardware.setDescription(hardwareForm.getDescription());
        if (hardwareForm.getCategory().getId() == -1)
            newHardware.setCategory(oldHardware.getCategory());
        else
            newHardware.setCategory(hardwareForm.getCategory());

        if (hardwareForm.getCharacteristics() == null)
            newHardware.setCharacteristics(oldHardware.getCharacteristics());
        else
            newHardware.setCharacteristics(hardwareForm.getCharacteristics());

        newHardware.setPrice(Double.parseDouble(hardwareForm.getPrice()));
        newHardware.setTotalCount(hardwareForm.getTotalCount());
        save(newHardware);
    }

    @Override
    public void delete(long id) {
        Hardware hardware = dao.findById(id);
        hardware.setCharacteristics(null);
        dao.save(hardware);
        log.info("Removing component data from the database\n" + hardware.toString());
        dao.deleteById(id);
    }

    @Override
    public List<Hardware> findByCharacteristicsAndCategoryId(List<Characteristic> characteristics, long id) {
        log.info("Taking data from the database (Accessories by category ID and characteristics)");
        return dao.findByCharacteristicsInAndCategory_Id(characteristics, id);
    }

    @Override
    public List<Hardware> checkOnCharacteristicHardware(List<Hardware> hardwares, List<Characteristic> characteristics) {
        log.info("Checking and removing hardwares if the number of characteristics of components does not match the number requested.");
        hardwares.removeIf(hardware -> hardware.getCharacteristics().size() < characteristics.size());
        return hardwares;
    }

    @Override
    public List<Hardware> findHardwaresByCharacteristicsIn(Characteristic characteristic) {
        log.info("Taking data from the database (Accessories by characteristics)");
        return dao.findAllByCharacteristics(characteristic);
    }

    @Override
    public void saveAll(List<Hardware> hardwares) {
        log.info("Writing component data in the database");
        dao.saveAll(hardwares);
    }

    private List<Hardware> deleteHardwareWhereCountEqualsNull(List<Hardware> hardwares) {
        hardwares.removeIf(hardware -> hardware.getTotalCount() == 0);
        return hardwares;
    }


}
