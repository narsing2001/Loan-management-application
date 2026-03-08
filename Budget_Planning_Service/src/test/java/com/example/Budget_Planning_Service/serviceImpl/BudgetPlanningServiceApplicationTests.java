package com.example.Budget_Planning_Service.serviceImpl;

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
import com.example.Budget_Planning_Service.service.serviceImpl.EmployeeBudgetServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeBudgetServiceImplTest {

	@Mock
	EmployeeBudgetRepository employeeRepo;
	@Mock
	AssetBudgetRepository assetRepo;
	@Mock
	AssetCostPurchaseRepository costRepo;

	@InjectMocks
	EmployeeBudgetServiceImpl service;

	// ----------------- Helpers -----------------
	private AssetCostPurchase cost(long id, String type, String amount) {
		AssetCostPurchase c = new AssetCostPurchase();
		c.setAssetType(type);
		c.setCostPrice(new BigDecimal(amount));
		return c;
	}

	private EmployeeBudget employee(long empId, BigDecimal total) {
		EmployeeBudget e = new EmployeeBudget();
		e.setEmployeeId(empId);
		e.setTotalBudget(total);
		return e;
	}

	private AssetBudget asset(long id, BigDecimal cost, AssetState state) {
		AssetBudget a = new AssetBudget();
		a.setAssetId(id);
		a.setCostPrice(cost);
		a.setState(state);
		return a;
	}

	// ----------------- ASSIGN ASSET -----------------

	@Test
	void assignAsset_success_newEmployee() {
		AssetCostPurchase cost = cost(2001L, "LAPTOP", "50000");

		when(employeeRepo.findByEmployeeId(1001L)).thenReturn(Optional.empty());
		when(costRepo.findById(2001L)).thenReturn(Optional.of(cost));
		when(employeeRepo.save(any())).thenAnswer(i -> i.getArgument(0));

		EmployeeBudgetResponse resp =
				service.assignAsset(new AssetAssignRequest(1001L, 2001L));

		assertEquals(1001L, resp.getEmployeeId());
		assertEquals(1, resp.getAssets().size());
		assertEquals(new BigDecimal("50000"), resp.getTotalBudget());
	}

	@Test
	void assignAsset_assetCostNotFound() {
		when(costRepo.findById(1L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class,
				() -> service.assignAsset(new AssetAssignRequest(10L, 1L)));
	}

	@Test
	void assignAsset_employeeExited() {
		EmployeeBudget emp = employee(10L, BigDecimal.ZERO);
		emp.setStatus(BudgetStatus.EXITED);

		when(employeeRepo.findByEmployeeId(10L)).thenReturn(Optional.of(emp));
		when(costRepo.findById(1L)).thenReturn(Optional.of(cost(1L, "LAPTOP", "1000")));

		assertThrows(IllegalStateException.class,
				() -> service.assignAsset(new AssetAssignRequest(10L, 1L)));
	}

	@Test
	void assignAsset_duplicateAsset() {
		EmployeeBudget emp = employee(10L, BigDecimal.ZERO);
		emp.getAssetBudgets().add(asset(1L, new BigDecimal("500"), AssetState.ASSIGNED));

		when(employeeRepo.findByEmployeeId(10L)).thenReturn(Optional.of(emp));
		when(costRepo.findById(1L)).thenReturn(Optional.of(cost(1L, "LAPTOP", "500")));

		assertThrows(IllegalStateException.class,
				() -> service.assignAsset(new AssetAssignRequest(10L, 1L)));
	}

	@Test
	void assignAsset_budgetExceeded() {
		EmployeeBudget emp = employee(10L, new BigDecimal("90000"));
		when(employeeRepo.findByEmployeeId(10L)).thenReturn(Optional.of(emp));
		when(costRepo.findById(2L)).thenReturn(Optional.of(cost(2L, "LAPTOP", "20000")));

		assertThrows(BudgetExceededException.class,
				() -> service.assignAsset(new AssetAssignRequest(10L, 2L)));
	}

	// ----------------- REPLACE ASSET -----------------

	@Test
	void replaceAsset_success() {
		EmployeeBudget emp = employee(1L, new BigDecimal("50000"));
		emp.getAssetBudgets().add(asset(101L, new BigDecimal("50000"), AssetState.ASSIGNED));

		when(employeeRepo.findByEmployeeId(1L)).thenReturn(Optional.of(emp));
		when(costRepo.findById(202L)).thenReturn(Optional.of(cost(202L, "LAPTOP", "20000")));
		when(employeeRepo.save(any())).thenAnswer(i -> i.getArgument(0));

		EmployeeBudgetResponse resp = service.replaceAsset(
				new AssetReplaceRequest(1L, 101L, 202L));

		assertEquals(2, resp.getAssets().size());
	}

	@Test
	void replaceAsset_oldAssetNotFound() {
		EmployeeBudget emp = employee(1L, BigDecimal.ZERO);

		when(employeeRepo.findByEmployeeId(1L)).thenReturn(Optional.of(emp));

		assertThrows(ResourceNotFoundException.class,
				() -> service.replaceAsset(new AssetReplaceRequest(1L, 10L, 20L)));
	}

	// ----------------- UPDATE STATE -----------------

	@Test
	void updateAssetState_success() {
		EmployeeBudget emp = employee(1L, new BigDecimal("1000"));
		emp.getAssetBudgets().add(asset(101L, new BigDecimal("1000"), AssetState.ASSIGNED));

		when(employeeRepo.findByEmployeeId(1L)).thenReturn(Optional.of(emp));
		when(employeeRepo.save(any())).thenAnswer(i -> i.getArgument(0));

		EmployeeBudgetResponse resp =
				service.updateAssetState(new AssetStateUpdateRequest(1L, 101L, AssetState.RETURNED));

		assertEquals(AssetState.RETURNED.name(),
				resp.getAssets().get(0).split(":")[1]);
	}

	@Test
	void updateAssetState_invalidTransition() {
		EmployeeBudget emp = employee(1L, BigDecimal.ZERO);
		emp.getAssetBudgets().add(asset(101L, new BigDecimal("1000"), AssetState.DECOMMISSIONED));

		when(employeeRepo.findByEmployeeId(1L)).thenReturn(Optional.of(emp));

		assertThrows(InvalidStateTransitionException.class,
				() -> service.updateAssetState(
						new AssetStateUpdateRequest(1L, 101L, AssetState.ASSIGNED)));
	}

	// ----------------- EXIT EMPLOYEE -----------------

	@Test
	void exitEmployee_success() {
		EmployeeBudget emp = employee(1L, new BigDecimal("2000"));
		emp.getAssetBudgets().add(asset(101L, new BigDecimal("2000"), AssetState.ASSIGNED));

		when(employeeRepo.findByEmployeeId(1L)).thenReturn(Optional.of(emp));
		when(employeeRepo.save(any())).thenAnswer(i -> i.getArgument(0));

		EmployeeBudgetResponse resp = service.exitEmployee(1L);

		assertEquals(0, resp.getTotalBudget().compareTo(BigDecimal.ZERO));
	}

	// ----------------- SUMMARY -----------------

	@Test
	void getCompanySummary_success() {
		when(assetRepo.findAll()).thenReturn(
				List.of(asset(1L, new BigDecimal("1000"), AssetState.ASSIGNED),
						asset(2L, new BigDecimal("2000"), AssetState.ASSIGNED)));

		CompanySummaryResponse resp = service.getCompanySummary();

		assertEquals(new BigDecimal("3000"), resp.getTotalBudget());
		assertEquals(2, resp.getTotalAssets());
	}

	@Test
	void getAssetSummary_success() {
		when(assetRepo.findByAssetId(1L)).thenReturn(
				List.of(asset(1L, new BigDecimal("500"), AssetState.ASSIGNED),
						asset(1L, new BigDecimal("1500"), AssetState.ASSIGNED)));

		AssetSummaryResponse resp = service.getAssetSummary(1L);

		assertEquals(new BigDecimal("2000"), resp.getTotalSpent());
		assertEquals(2, resp.getTotalCount());
	}

	@Test
	void getCategorySummary_success() {
		when(costRepo.findAll()).thenReturn(
				List.of(cost(1L, "LAPTOP", "1000"),
						cost(2L, "LAPTOP", "2000"),
						cost(3L, "MOBILE", "500")));

		CategorySummaryResponse resp = service.getCategorySummary("LAPTOP");

		assertEquals(2, resp.getTotalAssets());
		assertEquals(new BigDecimal("3000"), resp.getTotalCost());
	}
}
