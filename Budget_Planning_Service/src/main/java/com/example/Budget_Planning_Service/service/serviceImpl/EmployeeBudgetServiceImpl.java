package com.example.Budget_Planning_Service.service.serviceImpl;

import com.example.Budget_Planning_Service.constant.Constant;
import com.example.Budget_Planning_Service.dto.*;
import com.example.Budget_Planning_Service.exception.*;
import com.example.Budget_Planning_Service.model.AssetState;
import com.example.Budget_Planning_Service.model.BudgetStatus;
import com.example.Budget_Planning_Service.model.entity.AssetBudget;
import com.example.Budget_Planning_Service.model.entity.AssetCostPurchase;
import com.example.Budget_Planning_Service.model.entity.EmployeeBudget;
import com.example.Budget_Planning_Service.repository.AssetBudgetRepository;
import com.example.Budget_Planning_Service.repository.AssetCostPurchaseRepository;
import com.example.Budget_Planning_Service.repository.EmployeeBudgetRepository;
import com.example.Budget_Planning_Service.service.EmployeeBudgetService;
import com.example.Budget_Planning_Service.utility.EmployeeBudgetUtil;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
public class EmployeeBudgetServiceImpl implements EmployeeBudgetService {

    private final EmployeeBudgetRepository employeeBudgetRepository;
    private final AssetBudgetRepository assetBudgetRepository;
    private final AssetCostPurchaseRepository assetCostPurchaseRepository;


    private static final BigDecimal EMPLOYEE_MAX_BUDGET = new BigDecimal("100000.00");

    public EmployeeBudgetServiceImpl(EmployeeBudgetRepository employeeBudgetRepository,
                                     AssetBudgetRepository assetBudgetRepository,
                                     AssetCostPurchaseRepository assetCostPurchaseRepository) {
        this.employeeBudgetRepository = employeeBudgetRepository;
        this.assetBudgetRepository = assetBudgetRepository;
        this.assetCostPurchaseRepository = assetCostPurchaseRepository;
    }

    @Override
    public EmployeeBudgetResponse assignAsset(AssetAssignRequest request) {
        EmployeeBudget empBudget = employeeBudgetRepository.findByEmployeeId(request.getEmployeeId())
                .orElseGet(() -> {
                    EmployeeBudget e = new EmployeeBudget();
                    e.setEmployeeId(request.getEmployeeId());
                    return e;
                });


        AssetCostPurchase cost = assetCostPurchaseRepository.findById(request.getAssetId())
                .orElseThrow(() -> new ResourceNotFoundException(Constant.ASSET_COST_NOT_FOUND + request.getAssetId()));


        if (empBudget.getStatus() == BudgetStatus.EXITED) {
            throw new IllegalStateException(Constant.EMPLOYEE_EXITED_ASSIGN_NOT_ALLOWED);
        }


        boolean alreadyAssigned = empBudget.getAssetBudgets().stream()
                .anyMatch(ab -> ab.getAssetId().equals(request.getAssetId()) &&
                        (ab.getState() == AssetState.ASSIGNED || ab.getState() == AssetState.REPLACED));
        if (alreadyAssigned) {
            throw new IllegalStateException(Constant.ASSET_ALREADY_ASSIGNED);
        }


        BigDecimal newTotal = empBudget.getTotalBudget().add(cost.getCostPrice());
        if (newTotal.compareTo(EMPLOYEE_MAX_BUDGET) > 0) {
            throw new BudgetExceededException(Constant.ASSIGN_BUDGET_EXCEEDED);
        }


        AssetBudget ab = new AssetBudget();
        ab.setAssetId(request.getAssetId());
        ab.setCostPrice(cost.getCostPrice());
        ab.setState(AssetState.ASSIGNED);

        EmployeeBudgetUtil.addAssetBudget(ab, empBudget);
        EmployeeBudget saved = employeeBudgetRepository.save(empBudget);

        return toResponse(saved);
    }

    @Override
    public EmployeeBudgetResponse replaceAsset(AssetReplaceRequest request) {
        EmployeeBudget empBudget = employeeBudgetRepository.findByEmployeeId(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException(Constant.EMPLOYEE_NOT_FOUND));

        AssetBudget old = empBudget.getAssetBudgets().stream()
                .filter(a -> a.getAssetId().equals(request.getOldAssetId()) && a.getState() == AssetState.ASSIGNED)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(Constant.OLD_ASSET_NOT_FOUND));

        AssetCostPurchase newCost = assetCostPurchaseRepository.findById(request.getNewAssetId())
                .orElseThrow(() -> new ResourceNotFoundException(Constant.NEW_ASSET_COST_NOT_FOUND));


        old.setState(AssetState.REPLACED);


        AssetBudget newAb = new AssetBudget();
        newAb.setAssetId(request.getNewAssetId());
        newAb.setCostPrice(newCost.getCostPrice());
        newAb.setState(AssetState.ASSIGNED);
        EmployeeBudgetUtil.addAssetBudget(newAb, empBudget);


        if (empBudget.getTotalBudget().compareTo(EMPLOYEE_MAX_BUDGET) > 0) {
            throw new BudgetExceededException(Constant.ASSIGN_BUDGET_EXCEEDED);
        }

        EmployeeBudget saved = employeeBudgetRepository.save(empBudget);
        return toResponse(saved);
    }

    @Override
    public EmployeeBudgetResponse updateAssetState(AssetStateUpdateRequest request) {
        EmployeeBudget empBudget = employeeBudgetRepository.findByEmployeeId(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException(Constant.EMPLOYEE_NOT_FOUND));

        AssetBudget target = empBudget.getAssetBudgets().stream()
                .filter(a -> a.getAssetId().equals(request.getAssetId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(Constant.ASSET_ENTRY_NOT_FOUND));


        if (target.getState() == AssetState.DECOMMISSIONED && request.getState() == AssetState.ASSIGNED) {
            throw new InvalidStateTransitionException(Constant.INVALID_STATE_TRANSITION);
        }

        target.setState(request.getState());
        EmployeeBudgetUtil.recalcTotalBudget(empBudget);
        EmployeeBudget saved = employeeBudgetRepository.save(empBudget);
        return toResponse(saved);
    }

    @Override
    public EmployeeBudgetResponse getEmployeeBudget(Long employeeId) {
        EmployeeBudget empBudget = employeeBudgetRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(Constant.EMPLOYEE_NOT_FOUND));
        return toResponse(empBudget);
    }

    @Override
    public EmployeeBudgetResponse exitEmployee(Long employeeId) {
        EmployeeBudget empBudget = employeeBudgetRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(Constant.EMPLOYEE_NOT_FOUND));
        empBudget.getAssetBudgets().forEach(a -> a.setState(AssetState.DECOMMISSIONED));
        empBudget.setStatus(BudgetStatus.EXITED);
        EmployeeBudgetUtil.recalcTotalBudget(empBudget);
        EmployeeBudget saved = employeeBudgetRepository.save(empBudget);
        return toResponse(saved);
    }


    private EmployeeBudgetResponse toResponse(EmployeeBudget e) {
        EmployeeBudgetResponse resp = new EmployeeBudgetResponse();
        resp.setEmployeeId(e.getEmployeeId());
        resp.setTotalBudget(e.getTotalBudget());
        resp.setAssets(e.getAssetBudgets().stream()
                .map(a -> a.getAssetId() + ":" + a.getState() + ":" + a.getCostPrice())
                .collect(Collectors.toList()));
        return resp;
    }

    @Override
    public CompanySummaryResponse getCompanySummary() {

        List<AssetBudget> allAssets = assetBudgetRepository.findAll();

        BigDecimal total = allAssets.stream()
                .map(AssetBudget::getCostPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CompanySummaryResponse(total, allAssets.size());
    }

    @Override
    public AssetSummaryResponse getAssetSummary(Long assetId) {

        List<AssetBudget> assetEntries = assetBudgetRepository.findByAssetId(assetId);

        BigDecimal totalCost = assetEntries.stream()
                .map(AssetBudget::getCostPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new AssetSummaryResponse(assetId, totalCost, assetEntries.size());
    }

    @Override
    public CategorySummaryResponse getCategorySummary(String assetType) {

        List<AssetCostPurchase> list = assetCostPurchaseRepository.findAll()
                .stream()
                .filter(a -> a.getAssetType().equalsIgnoreCase(assetType))
                .toList();

        BigDecimal total = list.stream()
                .map(AssetCostPurchase::getCostPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CategorySummaryResponse(assetType, list.size(), total);
    }
}
