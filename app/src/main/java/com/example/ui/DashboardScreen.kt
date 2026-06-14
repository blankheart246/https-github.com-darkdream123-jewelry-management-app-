package com.example.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.core.content.FileProvider
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.Customer
import com.example.data.InventoryItem
import com.example.data.Transaction
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: JewelryViewModel, modifier: Modifier = Modifier) {
    val customers by viewModel.customers.collectAsState()
    val inventoryItems by viewModel.inventoryItems.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val branches by viewModel.branches.collectAsState()

    var activeTab by remember { mutableStateOf("home") } // "home", "catalog", "management", "finance", "customers"

    // Dynamic Business Config State
    val businessConfig by viewModel.businessConfig.collectAsState()

    // Dialog state controllers
    var showAddCustomerDialog by remember { mutableStateOf(false) }
    var showAddInventoryDialog by remember { mutableStateOf(false) }
    var showAddTransactionDialog by remember { mutableStateOf(false) }
    var selectedVisionBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showCustomerDetailsDialog by remember { mutableStateOf<Customer?>(null) }
    
    // Modification, deletion, and invoice view state controllers
    var showEditCustomerDialog by remember { mutableStateOf<Customer?>(null) }
    var showEditInventoryDialog by remember { mutableStateOf<InventoryItem?>(null) }
    var showBusinessConfigDialog by remember { mutableStateOf(false) }
    var showInvoicePreviewDialog by remember { mutableStateOf<Transaction?>(null) }
    var showGlobalSearchDialog by remember { mutableStateOf(false) }
    var showStockAlertsDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showChatSheet by remember { mutableStateOf(false) }
    var showLedgerForCustomer by remember { mutableStateOf<Customer?>(null) }
    var showAddSupplierDialog by remember { mutableStateOf(false) }
    var showAddArtisanDialog by remember { mutableStateOf(false) }
    var showAddEmployeeDialog by remember { mutableStateOf(false) }
    var showAddBranchDialog by remember { mutableStateOf(false) }
    var showAddBankAccountDialog by remember { mutableStateOf(false) }
    var showAddBusinessAccountDialog by remember { mutableStateOf(false) }

    val isOffline by viewModel.isOffline.collectAsState()
    val isDark by viewModel.isDarkMode.collectAsState()

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier.statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF7D5800)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Diamond,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = businessConfig.shopName,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F1B16),
                                    letterSpacing = (-0.2).sp,
                                    fontFamily = FontFamily.Serif
                                )
                                if (isOffline) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.CloudOff,
                                        contentDescription = "Offline Mode",
                                        tint = Color.Red,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Text(
                                text = "জুয়েলারি ব্যবসা পরিচালনা প্যানেল",
                                fontSize = 11.sp,
                                color = Color(0xFF7D5800),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val stockAlerts by viewModel.lowStockAlerts.collectAsState()
                        if (stockAlerts.isNotEmpty()) {
                            IconButton(onClick = { showStockAlertsDialog = true }) {
                                BadgedBox(
                                    badge = {
                                        Badge(containerColor = Color.Red) {
                                            Text(stockAlerts.size.toString(), color = Color.White)
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = "Notifications",
                                        tint = Color(0xFFBA1A1A)
                                    )
                                }
                            }
                        }
                        IconButton(onClick = { showGlobalSearchDialog = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Global Search", tint = Color(0xFF7D5800))
                        }
                        IconButton(onClick = { showSettingsDialog = true }) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color(0xFF7D5800))
                        }
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = Color(0xFFF3EFEA),
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .border(width = 0.5.dp, color = Color(0xFFEAE2D9))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Item 1: Home/Dashboard
                    val isHome = activeTab == "home"
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                                indication = null,
                                onClick = { activeTab = "home" }
                            )
                            .testTag("nav_desk")
                            .padding(vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isHome) Color(0xFFFFDDB3) else Color.Transparent)
                                .padding(horizontal = 20.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Dashboard",
                                tint = if (isHome) Color(0xFF291800) else Color(0xFF504539),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "ড্যাশবোর্ড",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isHome) Color(0xFF291800) else Color(0xFF504539)
                        )
                    }

                    // Item 2: Vault/Inventory
                    val isCatalog = activeTab == "catalog"
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                                indication = null,
                                onClick = { activeTab = "catalog" }
                            )
                            .testTag("nav_vault")
                            .weight(1f)
                            .padding(vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isCatalog) Color(0xFFFFDDB3) else Color.Transparent)
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Inventory2,
                                contentDescription = "Stock",
                                tint = if (isCatalog) Color(0xFF291800) else Color(0xFF504539),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "ক্যাটালগ",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCatalog) Color(0xFF291800) else Color(0xFF504539)
                        )
                    }

                    // Item: Management (Suppliers/Artisans)
                    val isManage = activeTab == "management"
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                                indication = null,
                                onClick = { activeTab = "management" }
                            )
                            .weight(1f)
                            .padding(vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isManage) Color(0xFFFFDDB3) else Color.Transparent)
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Engineering,
                                contentDescription = "Management",
                                tint = if (isManage) Color(0xFF291800) else Color(0xFF504539),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "ব্যবস্থাপনা",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isManage) Color(0xFF291800) else Color(0xFF504539)
                        )
                    }

                    // Item: Finance (Money/Bank)
                    val isFinance = activeTab == "finance"
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                                indication = null,
                                onClick = { activeTab = "finance" }
                            )
                            .weight(1f)
                            .padding(vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isFinance) Color(0xFFFFDDB3) else Color.Transparent)
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Payments,
                                contentDescription = "Finance",
                                tint = if (isFinance) Color(0xFF291800) else Color(0xFF504539),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "হিসাব",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isFinance) Color(0xFF291800) else Color(0xFF504539)
                        )
                    }

                    // Item 3: Clients
                    val isCustomers = activeTab == "customers"
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                                indication = null,
                                onClick = { activeTab = "customers" }
                            )
                            .testTag("nav_clients")
                            .weight(1f)
                            .padding(vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isCustomers) Color(0xFFFFDDB3) else Color.Transparent)
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Groups,
                                contentDescription = "Clients",
                                tint = if (isCustomers) Color(0xFF291800) else Color(0xFF504539),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "ক্রেতা",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCustomers) Color(0xFF291800) else Color(0xFF504539)
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            if (activeTab == "home") {
                FloatingActionButton(
                    onClick = { showChatSheet = true },
                    containerColor = Color(0xFF7D5800),
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Chat, contentDescription = "AI Assistant")
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeTab) {
                "home" -> HomeTabContent(
                    customers = customers,
                    inventoryItems = inventoryItems,
                    transactions = transactions,
                    viewModel = viewModel,
                    onRecordTxnClick = { showAddTransactionDialog = true },
                    onMetricClick = { target -> activeTab = target },
                    onBusinessCardClick = { showBusinessConfigDialog = true },
                    onTransactionClick = { txn -> showInvoicePreviewDialog = txn }
                )
                "catalog" -> CatalogTabContent(
                    viewModel = viewModel,
                    inventoryItems = inventoryItems,
                    branches = branches,
                    onAddNewClick = { showAddInventoryDialog = true },
                    onVisionScanClick = { bitmap ->
                        selectedVisionBitmap = bitmap
                        viewModel.analyzeJewelryImage(bitmap)
                    },
                    onItemClick = { showEditInventoryDialog = it }
                )
                "management" -> ManagementTabContent(
                    viewModel = viewModel,
                    onAddSupplier = { showAddSupplierDialog = true },
                    onAddArtisan = { showAddArtisanDialog = true },
                    onAddEmployee = { showAddEmployeeDialog = true },
                    onAddBranch = { showAddBranchDialog = true }
                )
                "finance" -> FinanceTabContent(
                    viewModel = viewModel,
                    onAddBankAccount = { showAddBankAccountDialog = true },
                    onAddBusinessAccount = { showAddBusinessAccountDialog = true }
                )
                "customers" -> CustomersTabContent(
                    viewModel = viewModel,
                    customers = customers,
                    onAddCustomerClick = { showAddCustomerDialog = true },
                    onCustomerClick = { showLedgerForCustomer = it }
                )
            }
        }
    }

    // --- DIALOGS ---

    if (showAddCustomerDialog) {
        AddCustomerDialog(
            onDismiss = { showAddCustomerDialog = false },
            onConfirm = { name, phone, email, notes ->
                viewModel.addCustomer(name, phone, email, notes)
                showAddCustomerDialog = false
            }
        )
    }

    if (showAddInventoryDialog) {
        AddInventoryDialog(
            branches = branches,
            onDismiss = { showAddInventoryDialog = false },
            onConfirm = { title, type, karat, weight, value, notes, tags, valBdt, paidBdt, dueBdt, base64, bId, bCode ->
                viewModel.addInventoryItem(
                    title = title,
                    itemType = type,
                    karat = karat,
                    weightGrams = weight,
                    estimatedValue = value,
                    notes = notes,
                    imageBase64 = base64,
                    tags = tags,
                    valueBdt = valBdt,
                    paidBdt = paidBdt,
                    dueBdt = dueBdt,
                    branchId = bId,
                    barcode = bCode
                )
                showAddInventoryDialog = false
            }
        )
    }

    if (showAddTransactionDialog) {
        AddTransactionDialog(
            customers = customers,
            inventoryItems = inventoryItems.filter { !it.isSold },
            onDismiss = { showAddTransactionDialog = false },
            onConfirm = { customerId, itemId, desc, type, amount, amtBdt, paidBdt, dueBdt, notes ->
                viewModel.addTransaction(
                    customerId = customerId,
                    inventoryItemId = itemId,
                    itemDescription = desc,
                    type = type,
                    amount = amount,
                    notes = notes,
                    amountBdt = amtBdt,
                    paidBdt = paidBdt,
                    dueBdt = dueBdt
                )
                showAddTransactionDialog = false
            }
        )
    }

    if (showAddSupplierDialog) {
        AddSupplierDialog(
            onDismiss = { showAddSupplierDialog = false },
            onConfirm = { name, contact, address ->
                viewModel.addSupplier(name, contact, address)
                showAddSupplierDialog = false
            }
        )
    }

    if (showAddArtisanDialog) {
        AddArtisanDialog(
            onDismiss = { showAddArtisanDialog = false },
            onConfirm = { name, contact, specialty ->
                viewModel.addArtisan(name, contact, specialty)
                showAddArtisanDialog = false
            }
        )
    }

    if (showAddEmployeeDialog) {
        AddEmployeeDialog(
            onDismiss = { showAddEmployeeDialog = false },
            onConfirm = { name, role, phone, salary ->
                viewModel.addEmployee(name, role, phone, salary)
                showAddEmployeeDialog = false
            }
        )
    }

    if (showAddBranchDialog) {
        AddBranchDialog(
            onDismiss = { showAddBranchDialog = false },
            onConfirm = { name, loc, phone ->
                viewModel.addBranch(name, loc, phone)
                showAddBranchDialog = false
            }
        )
    }

    if (showAddBankAccountDialog) {
        AddBankAccountDialog(
            onDismiss = { showAddBankAccountDialog = false },
            onConfirm = { name, no, bal ->
                viewModel.addBankAccount(name, no, bal)
                showAddBankAccountDialog = false
            }
        )
    }

    if (showAddBusinessAccountDialog) {
        AddBusinessAccountDialog(
            onDismiss = { showAddBusinessAccountDialog = false },
            onConfirm = { type, category, amount, notes ->
                viewModel.addBusinessAccount(type, category, amount, notes)
                showAddBusinessAccountDialog = false
            }
        )
    }

    if (showGlobalSearchDialog) {
        GlobalSearchDialog(
            viewModel = viewModel,
            onDismiss = { showGlobalSearchDialog = false }
        )
    }

    if (showStockAlertsDialog) {
        val stockAlerts by viewModel.lowStockAlerts.collectAsState()
        Dialog(onDismissRequest = { showStockAlertsDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "স্টক এলার্ট (সতর্কতা)",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFBA1A1A)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    if (stockAlerts.isEmpty()) {
                        Text("সব ক্যাটাগরির পর্যাপ্ত স্টক আছে।")
                    } else {
                        stockAlerts.forEach { (type, count) ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val localizedType = when (type) {
                                    "Ring" -> "আংটি"
                                    "Necklace" -> "নেকলেস"
                                    "Bracelet" -> "ব্রেসলেট"
                                    "Earrings" -> "দুল"
                                    "Pendant" -> "লকেট"
                                    else -> type
                                }
                                Text(localizedType, fontWeight = FontWeight.Medium)
                                Text(
                                    text = "বাকি আছে: $count টি",
                                    color = Color.Red,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Divider()
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { showStockAlertsDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7D5800))
                    ) {
                        Text("বন্ধ করুন")
                    }
                }
            }
        }
    }

    // Live AI Vision Extract Review Overlay
    val visionState by viewModel.aiVisionState.collectAsState()
    if (visionState !is AIVisionUiState.Idle && selectedVisionBitmap != null) {
        Dialog(onDismissRequest = {
            viewModel.clearVisionState()
            selectedVisionBitmap = null
        }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Gemini Vision Analytics",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Image(
                        bitmap = selectedVisionBitmap!!.asImageBitmap(),
                        contentDescription = "Selected piece",
                        modifier = Modifier
                            .height(160.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    when (val state = visionState) {
                        is AIVisionUiState.Idle -> {
                            // No analysis running
                        }
                        is AIVisionUiState.Loading -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(vertical = 24.dp)
                            ) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Analyzing gemstone setting, weight, and metal value...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        is AIVisionUiState.Success -> {
                            var title by remember { mutableStateOf(state.title) }
                            var itemType by remember { mutableStateOf(state.itemType) }
                            var karat by remember { mutableStateOf(state.karat) }
                            var weightGrams by remember { mutableStateOf(state.weightGrams.toString()) }
                            var estimatedValue by remember { mutableStateOf(state.estimatedValue.toString()) }
                            var notes by remember { mutableStateOf(state.notes) }

                            // Bangladesh Taka, customer details and tagging variables
                            var customerName by remember { mutableStateOf(state.customerName) }
                            var customerPhone by remember { mutableStateOf(state.customerPhone) }
                            var amountBdt by remember { mutableStateOf(state.amountBdt.toString()) }
                            var paidBdt by remember { mutableStateOf(state.paidBdt.toString()) }
                            var tags by remember { mutableStateOf(state.tags) }

                            val autoDueBdt = remember(amountBdt, paidBdt) {
                                val total = amountBdt.toDoubleOrNull() ?: 0.0
                                val paid = paidBdt.toDoubleOrNull() ?: 0.0
                                val diff = total - paid
                                if (diff < 0) "0.0" else String.format("%.2f", diff)
                            }

                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                label = { Text("Suggested Title") },
                                textStyle = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
                            )

                            Row(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = itemType,
                                    onValueChange = { itemType = it },
                                    label = { Text("Item Type") },
                                    textStyle = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f).padding(end = 4.dp, bottom = 6.dp)
                                )
                                OutlinedTextField(
                                    value = karat,
                                    onValueChange = { karat = it },
                                    label = { Text("Karat") },
                                    textStyle = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f).padding(start = 4.dp, bottom = 6.dp)
                                )
                            }

                            Row(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = weightGrams,
                                    onValueChange = { weightGrams = it },
                                    label = { Text("Weight (g)") },
                                    textStyle = MaterialTheme.typography.bodyMedium,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f).padding(end = 4.dp, bottom = 6.dp)
                                )
                                OutlinedTextField(
                                    value = estimatedValue,
                                    onValueChange = { estimatedValue = it },
                                    label = { Text("Value ($)") },
                                    textStyle = MaterialTheme.typography.bodyMedium,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f).padding(start = 4.dp, bottom = 6.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Patron & Bangladeshi Taka Ledger",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF7D5800),
                                modifier = Modifier.padding(bottom = 4.dp)
                            )

                            OutlinedTextField(
                                value = customerName,
                                onValueChange = { customerName = it },
                                label = { Text("Customer Name") },
                                textStyle = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
                            )

                            OutlinedTextField(
                                value = customerPhone,
                                onValueChange = { customerPhone = it },
                                label = { Text("Customer Phone") },
                                textStyle = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
                            )

                            Row(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = amountBdt,
                                    onValueChange = { amountBdt = it },
                                    label = { Text("Total Amount (৳ BDT)") },
                                    textStyle = MaterialTheme.typography.bodyMedium,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f).padding(end = 4.dp, bottom = 6.dp)
                                )
                                OutlinedTextField(
                                    value = paidBdt,
                                    onValueChange = { paidBdt = it },
                                    label = { Text("Amount Paid (৳ BDT)") },
                                    textStyle = MaterialTheme.typography.bodyMedium,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f).padding(start = 4.dp, bottom = 6.dp)
                                )
                            }

                            Text(
                                text = "Remaining Due Rest: ৳${amountBdt.toDoubleOrNull()?.minus(paidBdt.toDoubleOrNull() ?: 0.0)?.let { if (it < 0) 0.0 else it } ?: 0.0}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color(0xFFBA1A1A),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                            )

                            OutlinedTextField(
                                value = tags,
                                onValueChange = { tags = it },
                                label = { Text("Inventory Tags (comma separated)") },
                                textStyle = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                            )

                            OutlinedTextField(
                                value = notes,
                                onValueChange = { notes = it },
                                label = { Text("Atelier / Transcribe Notes") },
                                textStyle = MaterialTheme.typography.bodySmall,
                                maxLines = 3,
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                            )

                            Button(
                                onClick = {
                                    // Convert bitmap to Base64 to save in database
                                    val stream = ByteArrayOutputStream()
                                    selectedVisionBitmap!!.compress(Bitmap.CompressFormat.JPEG, 75, stream)
                                    val b64 = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)

                                    if (customerName.isNotBlank()) {
                                        viewModel.saveScannedBill(
                                            customerName = customerName,
                                            customerPhone = customerPhone,
                                            title = title,
                                            itemType = itemType,
                                            karat = karat,
                                            weightGrams = weightGrams.toDoubleOrNull() ?: state.weightGrams,
                                            estimatedValueUsd = estimatedValue.toDoubleOrNull() ?: state.estimatedValue,
                                            amountBdt = amountBdt.toDoubleOrNull() ?: 0.0,
                                            paidBdt = paidBdt.toDoubleOrNull() ?: 0.0,
                                            dueBdt = amountBdt.toDoubleOrNull()?.minus(paidBdt.toDoubleOrNull() ?: 0.0)?.let { if (it < 0) 0.0 else it } ?: 0.0,
                                            notes = notes,
                                            imageBase64 = b64,
                                            isCurrentlySold = true
                                        )
                                    } else {
                                        viewModel.addInventoryItem(
                                            title = title,
                                            itemType = itemType,
                                            karat = karat,
                                            weightGrams = weightGrams.toDoubleOrNull() ?: state.weightGrams,
                                            estimatedValue = estimatedValue.toDoubleOrNull() ?: state.estimatedValue,
                                            notes = notes,
                                            imageBase64 = b64,
                                            tags = tags,
                                            valueBdt = amountBdt.toDoubleOrNull() ?: 0.0,
                                            paidBdt = paidBdt.toDoubleOrNull() ?: 0.0,
                                            dueBdt = amountBdt.toDoubleOrNull()?.minus(paidBdt.toDoubleOrNull() ?: 0.0)?.let { if (it < 0) 0.0 else it } ?: 0.0
                                        )
                                    }
                                    viewModel.clearVisionState()
                                    selectedVisionBitmap = null
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Accept Draft & Log entries", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                        is AIVisionUiState.Error -> {
                            Text(
                                text = state.message,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                            Button(
                                onClick = {
                                    viewModel.clearVisionState()
                                    selectedVisionBitmap = null
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Close")
                            }
                        }
                    }
                }
            }
        }
    }

    // Customer Detailed Modal with AI Assistant Report
    if (showCustomerDetailsDialog != null) {
        val customer = showCustomerDetailsDialog!!
        val aiSearchState by viewModel.aiSearchState.collectAsState()

        Dialog(onDismissRequest = {
            showCustomerDetailsDialog = null
            viewModel.clearSearchState()
        }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "গ্রাহক প্রোফাইল বিবরণী",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    showEditCustomerDialog = customer
                                }
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "সম্পাদনা করুন", tint = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(
                                onClick = {
                                    showCustomerDetailsDialog = null
                                    viewModel.clearSearchState()
                                }
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = customer.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(text = "ইমেইল: " + customer.email, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "মোবাইল: " + customer.phone, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "খদ্দেরের ধরন ও বিশেষ নোট: " + customer.notes, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))

                    val clientTransactions = remember(transactions, customer.id) {
                        transactions.filter { it.customerId == customer.id }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "গ্রাহকের লেনদেন খতিয়ান সূচি (অনলাইন চালান)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7D5800),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    val totBdt = clientTransactions.sumOf { it.amountBdt }
                    val paidBdt = clientTransactions.sumOf { it.paidBdt }
                    val dueBdt = clientTransactions.sumOf { it.dueBdt }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF6F0)),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                Text("মোট", fontSize = 10.sp, color = Color(0xFF85735E))
                                Text("৳${String.format("%,.0f", totBdt)}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                Text("পরিশোধিত", fontSize = 10.sp, color = Color(0xFF85735E))
                                Text("৳${String.format("%,.0f", paidBdt)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B6B2C))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                Text("অবশিষ্ট দেনা", fontSize = 10.sp, color = Color(0xFF85735E))
                                Text("৳${String.format("%,.0f", dueBdt)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFBA1A1A))
                            }
                        }
                    }

                    if (clientTransactions.isEmpty()) {
                        Text(
                            text = "এই খদ্দেরের কোনো লেনদেন রেকর্ড নেই।",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    } else {
                        clientTransactions.forEach { tx ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                                border = BorderStroke(1.dp, Color(0xFFF3EFEA)),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = tx.itemDescription, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                        Text(text = if (tx.transactionType == "Purchase" || tx.transactionType == "বিক্রয়") "বিক্রয়" else tx.transactionType, fontSize = 9.sp, color = Color(0xFF85735E))
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(text = "৳${String.format("%,.0f", tx.amountBdt)}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        if (tx.dueBdt > 0.0) {
                                            Text(text = "বকেয়া: ৳${String.format("%,.0f", tx.dueBdt)}", fontSize = 9.sp, color = Color(0xFFBA1A1A), fontWeight = FontWeight.Bold)
                                        } else {
                                            Text(text = "সম্পূর্ণ পরিশোধিত", fontSize = 9.sp, color = Color(0xFF1B6B2C), fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(16.dp))

                    // AI Section trigger
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Gemini Client Intelligence",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = { viewModel.searchCustomerHistory(customer.name) },
                            colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "Run Client Advisor Agent", tint = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    when (val aiState = aiSearchState) {
                        is AISearchUiState.Idle -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Tap the ✨ button to request Gemini to synthesize this customer's preferences, transaction values, and suggest custom items to pitch.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        is AISearchUiState.Loading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Synthesizing records with Gemini...", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                        is AISearchUiState.Success -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
                                    .border(0.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("AI EXECUTIVE REPORT:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = aiState.summary,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                        is AISearchUiState.Error -> {
                            Text(
                                text = aiState.message,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }

    if (showBusinessConfigDialog) {
        BusinessConfigDialog(
            config = businessConfig,
            onDismiss = { showBusinessConfigDialog = false },
            onConfirm = {
                viewModel.updateBusinessConfig(it)
                showBusinessConfigDialog = false
            }
        )
    }

    if (showEditCustomerDialog != null) {
        EditCustomerDialog(
            customer = showEditCustomerDialog!!,
            onDismiss = { showEditCustomerDialog = null },
            onConfirm = {
                viewModel.updateCustomer(it)
                showEditCustomerDialog = null
                // Also update the shown detail dialog if it corresponds to same customer
                if (showCustomerDetailsDialog?.id == it.id) {
                    showCustomerDetailsDialog = it
                }
            },
            onDelete = {
                viewModel.deleteCustomer(it)
                showEditCustomerDialog = null
                showCustomerDetailsDialog = null
            }
        )
    }

    if (showEditInventoryDialog != null) {
        EditInventoryDialog(
            item = showEditInventoryDialog!!,
            customers = customers,
            onDismiss = { showEditInventoryDialog = null },
            onConfirm = {
                viewModel.updateInventoryItem(it)
                showEditInventoryDialog = null
            },
            onDelete = {
                viewModel.deleteInventoryItem(it)
                showEditInventoryDialog = null
            }
        )
    }

    if (showInvoicePreviewDialog != null) {
        InvoicePreviewDialog(
            txn = showInvoicePreviewDialog!!,
            customers = customers,
            businessConfig = businessConfig,
            onDismiss = { showInvoicePreviewDialog = null },
            onPayDue = { transaction, clearAmount, notes ->
                viewModel.recordDuePayment(transaction, clearAmount, notes)
                showInvoicePreviewDialog = null
            }
        )
    }

    if (showSettingsDialog) {
        SettingsDialog(
            viewModel = viewModel,
            onDismiss = { showSettingsDialog = false }
        )
    }

    if (showChatSheet) {
        AIAssistantChatSheet(
            viewModel = viewModel,
            onDismiss = { showChatSheet = false }
        )
    }

    showLedgerForCustomer?.let { customer ->
        CustomerLedgerDialog(
            viewModel = viewModel,
            customer = customer,
            onDismiss = { showLedgerForCustomer = null }
        )
    }

    val isVoiceActive by viewModel.isVoiceActive.collectAsState()
    if (isVoiceActive) {
        VoiceInteractionOverlay(
            viewModel = viewModel,
            onDismiss = { viewModel.clearVoiceState() }
        )
    }
}
@Composable
fun HomeTabContent(
    customers: List<Customer>,
    inventoryItems: List<InventoryItem>,
    transactions: List<Transaction>,
    viewModel: JewelryViewModel,
    onRecordTxnClick: () -> Unit,
    onMetricClick: (targetTab: String) -> Unit,
    onBusinessCardClick: () -> Unit,
    onTransactionClick: (Transaction) -> Unit
) {
    val businessConfig by viewModel.businessConfig.collectAsState()

    // BDT valuations based on inventory valueBdt values
    val totalBdtValue = remember(inventoryItems) {
        inventoryItems.sumOf { it.valueBdt }
    }
    // USD valuations as fallback
    val totalInventoryValue = remember(inventoryItems) {
        inventoryItems.sumOf { it.estimatedValue }
    }

    val unsoldItemsCount = remember(inventoryItems) {
        inventoryItems.count { !it.isSold }
    }

    val totalGoldWeight = remember(inventoryItems) {
        inventoryItems.filter { !it.isSold }.sumOf { it.weightGrams }
    }

    val karatDistribution = remember(inventoryItems) {
        inventoryItems.filter { !it.isSold }
            .groupBy { it.karat }
            .mapValues { it.value.size }
    }

    // BDT sales based on transaction amount BDT
    val totalBdtSales = remember(transactions) {
        transactions.sumOf { it.amountBdt }
    }
    val totalTransactionsAmount = remember(transactions) {
        transactions.sumOf { it.amount }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            // Welcome Card in Clean Minimalism Gold - Clickable!
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xFFFFDDB3))
                    .border(1.dp, Color(0xFFF0E0CF), RoundedCornerShape(28.dp))
                    .clickable { onBusinessCardClick() }
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF7D5800)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Diamond,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0xFFF0E0CF))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "স্বর্ণালি এআই রেডি",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF291800),
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = businessConfig.shopName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF291800),
                        fontFamily = FontFamily.Serif
                    )
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { viewModel.fetchLiveGoldRate() }) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("লাইভ গোল্ড রেট দেখুন", fontSize = 10.sp, color = Color(0xFF7D5800))
                        }
                    }
                    Text(
                        text = "স্বত্বাধিকারী: ${businessConfig.ownerName} | মোবাইল: ${businessConfig.phone}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF504539),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "আজকের সোনার ভরি রেট (২২ক্যারেট): ৳ ${businessConfig.goldRate22K} টাকা",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7D5800)
                    )
                    Text(
                        text = "স্বর্ণালঙ্কার ভ্যালুয়েশন, খদ্দেরের খতিয়ান রশিদ এবং ক্যাশ মেমো তৈরির জন্য প্যানেল। ব্যবসার নাম ও রেট পরিবর্তন করতে এখানে আলতো চাপুন।",
                        fontSize = 11.sp,
                        color = Color(0xFF504539),
                        modifier = Modifier.padding(top = 6.dp),
                        lineHeight = 15.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "কুইক স্মার্ট অ্যাকশন (Smart AI)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF7D5800),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Voice Command Card
                Card(
                    modifier = Modifier.weight(1f).clickable { viewModel.setVoiceActive(true) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = null, tint = Color(0xFF7D5800))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("ভয়েস কমান্ড", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                
                // AI Photo Scan Card
                Card(
                    modifier = Modifier.weight(1f).clickable { onMetricClick("catalog") },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = Color(0xFF7D5800))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("কুইক স্ক্যান", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            GoldCalculatorWidget(businessConfig = businessConfig)

            Spacer(modifier = Modifier.height(16.dp))

            // --- Business Performance Summary Widget ---
            Text(
                text = "আজকের ব্যবসায়িক পারফরম্যান্স (Daily Stats)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF7D5800),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            val dailyRevenue by viewModel.dailyRevenue.collectAsState()
            val dailyItemsSold by viewModel.dailyItemsSold.collectAsState()

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF2E6)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFE6DBC9))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color(0xFF2E7D32))
                        Text("মোট বিক্রি", fontSize = 10.sp, color = Color.Gray)
                        Text(
                            text = "৳ ${String.format("%,.0f", dailyRevenue)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                    Divider(modifier = Modifier.height(30.dp).width(1.dp), color = Color(0xFFE6DBC9))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Inventory, contentDescription = null, tint = Color(0xFF765B48))
                        Text("আইটেম বিক্রি", fontSize = 10.sp, color = Color.Gray)
                        Text(
                            text = "$dailyItemsSold টি",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF765B48)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Metrics Grid (2x2 style) - Clickable!
            Row(modifier = Modifier.fillMaxWidth()) {
                MetricCard(
                    title = "মোট বর্তমান স্টক স্টক মূল্য",
                    value = "৳ ${String.format("%,.0f", inventoryItems.filter { !it.isSold }.sumOf { it.valueBdt })}",
                    icon = Icons.Default.Inventory,
                    subtitle = "ভল্টে অবিক্রিত গহনার মূল্য",
                    onClick = { onMetricClick("catalog") },
                    modifier = Modifier.weight(1f).padding(end = 6.dp)
                )
                MetricCard(
                    title = "মোট স্বর্ণের ওজন (মজুদ)",
                    value = "${String.format("%.2f", totalGoldWeight)} গ্রাম",
                    icon = Icons.Default.MonitorWeight,
                    subtitle = "মোট মজুদ স্বর্ণের পরিমাণ",
                    onClick = { onMetricClick("catalog") },
                    modifier = Modifier.weight(1f).padding(start = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Karat-wise distribution chips
            if (karatDistribution.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF6F0)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "ক্যারেট ভিত্তিক মজুত বিভাজন", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7D5800))
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            karatDistribution.forEach { (karat, count) ->
                                Surface(
                                    color = Color.White,
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, Color(0xFFEAE2D9))
                                ) {
                                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = karat, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Badge(containerColor = Color(0xFFFFDDB3), contentColor = Color(0xFF291800)) {
                                            Text(text = count.toString(), fontSize = 9.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                MetricCard(
                    title = "অবিক্রিত গহনা মজুদ",
                    value = "$unsoldItemsCount টি গহনা",
                    icon = Icons.Default.Storefront,
                    subtitle = "ভল্টে বিক্রয়ের জন্য প্রস্তুত",
                    onClick = { onMetricClick("catalog") },
                    modifier = Modifier.weight(1f).padding(end = 6.dp)
                )
                MetricCard(
                    title = "নিবন্ধিত খদ্দের সংখ্যা",
                    value = "${customers.size} জন গ্রাহক",
                    icon = Icons.Default.PeopleOutline,
                    subtitle = "মূল্যবান গ্রাহক ডাটাবেস",
                    onClick = { onMetricClick("customers") },
                    modifier = Modifier.weight(1f).padding(start = 6.dp)
                )
            }

            PnLSummaryCard(viewModel)
            
            CategoryDistributionChart(inventoryItems = inventoryItems)

            Spacer(modifier = Modifier.height(16.dp))

            // AI Business Assistant Section
            val businessAdviceState by viewModel.aiBusinessAdviceState.collectAsState()
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3EFEA)),
                border = BorderStroke(1.dp, Color(0xFFEAE2D9))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF7D5800), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "স্বর্ণালি এআই বিজনেস অ্যাসিস্ট্যান্ট",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF291800)
                            )
                        }
                        IconButton(
                            onClick = { viewModel.generateBusinessAdvice() },
                            modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0xFFFFDDB3))
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "বিশ্লেষণ শুরু করুন", tint = Color(0xFF291800), modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    when (val state = businessAdviceState) {
                        is AIBusinessAdviceState.Idle -> {
                            Text(
                                text = "আপনার দোকানের স্টক এবং বিক্রয় তথ্যের উপর ভিত্তি করে ব্যবসার পরামর্শ পেতে আলতো চাপুন।",
                                fontSize = 11.sp,
                                color = Color(0xFF85735E)
                            )
                        }
                        is AIBusinessAdviceState.Loading -> {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color(0xFF7D5800))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Gemini তথ্য বিশ্লেষণ করছে...", fontSize = 11.sp, color = Color(0xFF85735E))
                            }
                        }
                        is AIBusinessAdviceState.Success -> {
                            Text(
                                text = state.advice,
                                fontSize = 12.sp,
                                color = Color(0xFF1F1B16),
                                lineHeight = 16.sp
                            )
                            TextButton(
                                onClick = { viewModel.clearBusinessAdvice() },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("বন্ধ করুন", fontSize = 11.sp, color = Color(0xFF7D5800))
                            }
                        }
                        is AIBusinessAdviceState.Error -> {
                            Text(text = "ত্রুটি: ${state.message}", fontSize = 11.sp, color = Color.Red)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action section row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "সাম্প্রতিক লেনদেন খতিয়ান (মেমো খাম)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = MaterialTheme.colorScheme.primary
                )

                Button(
                    onClick = onRecordTxnClick,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("record_txn_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("মেমো তৈরি করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        if (transactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "কোনো লেনদেনের মেমো পাওয়া যায়নি",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(transactions) { txn ->
                val customerName = customers.firstOrNull { it.id == txn.customerId }?.name ?: "অজানা গ্রাহক"
                val initials = remember(customerName) {
                    customerName.split(" ")
                        .filter { it.isNotBlank() }
                        .mapNotNull { it.firstOrNull() }
                        .joinToString("")
                        .uppercase()
                        .take(2)
                        .ifEmpty { "ক" }
                }

                val avatarBg = if (txn.transactionType == "Purchase" || txn.transactionType == "বিক্রয়") Color(0xFFEADDFF) else Color(0xFFD1E4FF)
                val avatarText = if (txn.transactionType == "Purchase" || txn.transactionType == "বিক্রয়") Color(0xFF21005D) else Color(0xFF001D36)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(Color.White, RoundedCornerShape(20.dp))
                        .border(1.dp, Color(0xFFF3EFEA), RoundedCornerShape(20.dp))
                        .clickable { onTransactionClick(txn) } // Dialog cash memo view trigger!
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(avatarBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = avatarText
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = customerName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1F1B16)
                        )
                        Text(
                            text = txn.itemDescription,
                            fontSize = 12.sp,
                            color = Color(0xFF85735E),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "৳ ${String.format("%,.0f", txn.amountBdt)}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F1B16)
                        )
                        val formattedDate = remember(txn.date) {
                            SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(txn.date))
                        }
                        Text(
                            text = formattedDate,
                            fontSize = 10.sp,
                            color = Color(0xFF85735E)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(115.dp)
            .clickable { onClick() }
            .testTag("metric_" + title.replace(" ", "_").lowercase()),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color(0xFFF3EFEA))
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title.uppercase(Locale.ROOT),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = Color(0xFF85735E),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color(0xFF7D5800),
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = value,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F1B16),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Text(
                text = subtitle,
                fontSize = 9.sp,
                color = Color(0xFF85735E),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ------ TAB 2: CATALOG (Inventory tracking and Vision) ------
@Composable
fun CatalogTabContent(
    viewModel: JewelryViewModel,
    inventoryItems: List<InventoryItem>,
    branches: List<com.example.data.Branch>,
    onAddNewClick: () -> Unit,
    onVisionScanClick: (Bitmap) -> Unit,
    onItemClick: (InventoryItem) -> Unit
) {
    val context = LocalContext.current

    // Launchers for picking/getting images
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                if (bitmap != null) {
                    onVisionScanClick(bitmap)
                }
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            onVisionScanClick(it)
        }
    }

    var selectedFilterType by remember { mutableStateOf("All") }
    var selectedTags by remember { mutableStateOf(setOf<String>()) }
    var selectedBranchFilterId by remember { mutableStateOf<Long?>(null) }

    // Aggregate all unique, clean tags across all available inventory items
    val allDistinctTags = remember(inventoryItems) {
        inventoryItems.flatMap { item ->
            item.tags.split(",")
                .map { it.trim().lowercase() }
                .filter { it.isNotEmpty() }
        }.distinct().sorted()
    }

    val filteredItems = remember(inventoryItems, selectedFilterType, selectedTags, selectedBranchFilterId) {
        val branchFiltered = if (selectedBranchFilterId == null) {
            inventoryItems
        } else {
            inventoryItems.filter { it.branchId == selectedBranchFilterId }
        }

        val typeFiltered = if (selectedFilterType == "All") {
            branchFiltered
        } else {
            branchFiltered.filter { it.itemType.equals(selectedFilterType, ignoreCase = true) }
        }
        if (selectedTags.isEmpty()) {
            typeFiltered
        } else {
            typeFiltered.filter { item ->
                val itemTags = item.tags.split(",")
                    .map { it.trim().lowercase() }
                    .filter { it.isNotEmpty() }
                    .toSet()
                selectedTags.all { tag -> itemTags.contains(tag) }
            }
        }
    }

    val itemTypes = listOf("All", "Ring", "Necklace", "Bracelet", "Earrings", "Pendant")

    val aiFilteredItems by viewModel.aiFilteredInventoryItems.collectAsState()
    val aiSearchState by viewModel.aiInventorySearchState.collectAsState()

    var aiSearchInput by remember { mutableStateOf("") }
    var showStats by remember { mutableStateOf(false) }

    // Calculate dynamic jewelry stock summary statistics
    val unsoldItems = remember(inventoryItems) { inventoryItems.filter { !it.isSold } }
    val totalUnsoldWeight = remember(unsoldItems) { unsoldItems.sumOf { it.weightGrams } }
    val totalUnsoldBhari = remember(totalUnsoldWeight) { totalUnsoldWeight / 11.664 }
    val totalUnsoldItemsCount = unsoldItems.size

    val karatBreakdown = remember(unsoldItems) {
        unsoldItems.groupBy { it.karat }.mapValues { group ->
            Pair(group.value.size, group.value.sumOf { it.weightGrams })
        }
    }

    val displayedItems = aiFilteredItems ?: filteredItems

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // AI Natural Language Search Bar
        OutlinedTextField(
            value = aiSearchInput,
            onValueChange = { 
                aiSearchInput = it
                if (it.isEmpty()) viewModel.clearInventorySearch()
            },
            placeholder = { Text("এআই সার্চ: '২২ ক্যারেট লকেট' বা 'Rings under 50k'", fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF7D5800), modifier = Modifier.size(18.dp)) },
            trailingIcon = {
                if (aiSearchInput.isNotEmpty()) {
                    IconButton(onClick = { 
                        viewModel.searchInventoryNaturalLanguage(aiSearchInput)
                    }) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Search", tint = Color(0xFF7D5800))
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF7D5800),
                unfocusedBorderColor = Color(0xFFEAE2D9)
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Search),
            keyboardActions = androidx.compose.foundation.text.KeyboardActions(onSearch = {
                viewModel.searchInventoryNaturalLanguage(aiSearchInput)
            })
        )

        if (aiFilteredItems != null) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = when (val s = aiSearchState) {
                        is AIInventorySearchState.Success -> s.aiMessage
                        else -> "পছন্দসই গহনা ফিল্টার করা হয়েছে"
                    },
                    fontSize = 11.sp,
                    color = Color(0xFF7D5800),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = { 
                    aiSearchInput = ""
                    viewModel.clearInventorySearch()
                }) {
                    Text("মুছে ফেলুন", fontSize = 11.sp)
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "মজুদ গচ্ছিত গহনা (ভল্ট)",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = MaterialTheme.colorScheme.primary
            )

            Row {
                IconButton(
                    onClick = { cameraLauncher.launch(null) },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.testTag("scan_camera_button")
                ) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = "Camera Scan", tint = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.testTag("scan_gallery_button")
                ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = "Gallery Scan", tint = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Button(
                    onClick = onAddNewClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.testTag("add_inventory_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("যোগ করুন", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- Current Jewelry Stock Statistics Panel ---
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFFF3EFEA)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showStats = !showStats },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = null,
                            tint = Color(0xFF7D5800),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "মজুদ ও ভরি পরিসংখ্যান (ভল্ট সামারি)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F1B16)
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (showStats) "আড়াল করুন" else "বিস্তারিত দেখুন",
                            fontSize = 11.sp,
                            color = Color(0xFF7D5800)
                        )
                        Icon(
                            imageVector = if (showStats) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color(0xFF7D5800),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("মোট অবিক্রিত গহনা", fontSize = 10.sp, color = Color(0xFF85735E))
                        Text("$totalUnsoldItemsCount টি গহনা", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F1B16))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("মোট মজুদ ভর (ওজন)", fontSize = 10.sp, color = Color(0xFF85735E))
                        Text(
                            text = String.format("%.2f গ্রাম", totalUnsoldWeight),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F1B16)
                        )
                        Text(
                            text = String.format("(~%.2f ভরি)", totalUnsoldBhari),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF7D5800)
                        )
                    }
                }

                if (showStats && karatBreakdown.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "সোনার ক্যারেট অনুযায়ী মজুদ বিভাজন:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF7D5800),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        karatBreakdown.forEach { (karat, stat) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "• $karat",
                                    fontSize = 12.sp,
                                    color = Color(0xFF1F1B16),
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "${stat.first} টি (${String.format("%.2f", stat.second)} গ্রাম / ~${String.format("%.2f", stat.second / 11.664)} ভরি)",
                                    fontSize = 12.sp,
                                    color = Color(0xFF504539)
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- AI Natural Language Search Bar ---
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9EE)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFFFFE7C4)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "✨ এআই দিয়ে খুঁজুন (যেকোনো ভাষায় বার্তার মতো লিখুন)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF7D5800)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = aiSearchInput,
                        onValueChange = { aiSearchInput = it },
                        placeholder = { Text("যেমন: show me 22k gold rings বা সোনার আংটি দেখান", fontSize = 12.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("ai_inventory_search_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF7D5800),
                            focusedLabelColor = Color(0xFF7D5800),
                            unfocusedBorderColor = Color(0xFFE5D5C5),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                        trailingIcon = {
                            if (aiSearchInput.isNotEmpty() || aiFilteredItems != null) {
                                IconButton(onClick = {
                                    aiSearchInput = ""
                                    viewModel.clearInventorySearch()
                                }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            viewModel.searchInventoryNaturalLanguage(aiSearchInput)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7D5800)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(52.dp)
                            .testTag("ai_inventory_search_submit"),
                        contentPadding = PaddingValues(horizontal = 14.dp)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("খুঁজুন", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                when (val state = aiSearchState) {
                    is AIInventorySearchState.Loading -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = Color(0xFF7D5800)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("এআই খতিয়ান বিশ্লেষণ করছে...", fontSize = 12.sp, color = Color(0xFF85735E))
                        }
                    }
                    is AIInventorySearchState.Success -> {
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            Text(
                                text = state.aiMessage,
                                fontSize = 12.sp,
                                color = Color(0xFF1B6B2C),
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            TextButton(
                                onClick = {
                                    aiSearchInput = ""
                                    viewModel.clearInventorySearch()
                                },
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier.height(24.dp)
                            ) {
                                Text("স্বাভাবিক তালিকায় ফিরুন (রিসেট)", fontSize = 11.sp, color = Color(0xFFBA1A1A))
                            }
                        }
                    }
                    is AIInventorySearchState.Error -> {
                        Text(
                            text = "অনুসন্ধান ব্যর্থ হয়েছে: ${state.message}",
                            fontSize = 12.sp,
                            color = Color(0xFFBA1A1A),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    else -> {}
                }
            }
        }

        // Filters horizontal row scrollable
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Branch Filter
            FilterChip(
                selected = selectedBranchFilterId == null,
                onClick = { selectedBranchFilterId = null },
                label = { Text("সকল শাখা", fontSize = 11.sp) },
                leadingIcon = { Icon(Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            branches.forEach { branch ->
                FilterChip(
                    selected = selectedBranchFilterId == branch.id,
                    onClick = { selectedBranchFilterId = branch.id },
                    label = { Text(branch.branchName, fontSize = 11.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemTypes.forEach { type ->
                val localizedType = when (type) {
                    "All" -> "সব গহনা"
                    "Ring" -> "আংটি"
                    "Necklace" -> "নেকলেস"
                    "Bracelet" -> "ব্রেসলেট"
                    "Earrings" -> "দুল (ঝুমকা)"
                    "Pendant" -> "লকেট"
                    else -> type
                }
                FilterChip(
                    selected = selectedFilterType == type,
                    onClick = { 
                        selectedFilterType = type 
                        // If AI search is active, do a quick filter or reset
                    },
                    label = { Text(localizedType) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        selectedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }

        // Tag Filters horizontal row scrollable representation
        if (allDistinctTags.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "ট্যাগ ফিল্টার করুন (${selectedTags.size}টি সক্রিয়)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF7D5800)
                )
                if (selectedTags.isNotEmpty()) {
                    TextButton(
                        onClick = { selectedTags = emptySet() },
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.height(24.dp)
                    ) {
                        Text("সব মুছুন", fontSize = 10.sp, color = Color(0xFFBA1A1A))
                    }
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                allDistinctTags.forEach { tag ->
                    val isSelected = selectedTags.contains(tag)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedTags = if (isSelected) selectedTags - tag else selectedTags + tag
                        },
                        label = { Text("#$tag", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFFDDB3),
                            selectedLabelColor = Color(0xFF291800),
                            containerColor = Color.White,
                            labelColor = Color(0xFF504539)
                        ),
                        border = BorderStroke(1.dp, Color(0xFFEAE2D9))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (displayedItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Inventory2,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "কোনো গহনা খুঁজে পাওয়া যায়নি।\nনতুন গহনা যোগ করতে বা এআই দ্বারা স্ক্যান করতে বোতামগুলো ব্যবহার করুন!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(displayedItems) { item ->
                    InventoryItemCard(item = item, onClick = { onItemClick(item) })
                }
            }
        }
    }
}

// ------ TAB 3: CLIENTS DIRECTORY (Customer management) ------
@Composable
fun CustomersTabContent(
    viewModel: JewelryViewModel,
    customers: List<Customer>,
    onAddCustomerClick: () -> Unit,
    onCustomerClick: (Customer) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredCustomers = remember(customers, searchQuery) {
        if (searchQuery.isBlank()) customers
        else customers.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Client Registry",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            )

            Button(
                onClick = onAddCustomerClick,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("add_client_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by customer name...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (filteredCustomers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.PeopleOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No clients matching '$searchQuery' found.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredCustomers) { customer ->
                    val initials = remember(customer.name) {
                        customer.name.split(" ")
                            .filter { it.isNotBlank() }
                            .mapNotNull { it.firstOrNull() }
                            .joinToString("")
                            .uppercase()
                            .take(2)
                            .ifEmpty { "C" }
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFF3EFEA)),
                        shape = RoundedCornerShape(24.dp),
                        onClick = { onCustomerClick(customer) },
                        modifier = Modifier.testTag("client_card_${customer.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF3EFEA)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = initials,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF291800),
                                    fontSize = 15.sp,
                                    fontFamily = FontFamily.Serif
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = customer.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1F1B16)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = customer.phone,
                                    fontSize = 12.sp,
                                    color = Color(0xFF85735E)
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.KeyboardArrowRight,
                                contentDescription = "View details",
                                tint = Color(0xFF7D5800)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InventoryItemCard(item: com.example.data.InventoryItem, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF3EFEA)),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .clickable { onClick() }
            .testTag("item_card_${item.id}")
    ) {
        Column {
            if (!item.imageBase64.isNullOrEmpty()) {
                Base64Image(
                    base64Str = item.imageBase64,
                    modifier = Modifier
                        .height(120.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .height(120.dp)
                        .fillMaxWidth()
                        .background(Color(0xFFF3EFEA)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Diamond,
                        contentDescription = null,
                        tint = Color(0xFF7D5800).copy(alpha = 0.5f),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val localizedType = when (item.itemType) {
                        "Ring" -> "আংটি"
                        "Necklace" -> "নেকলেস"
                        "Bracelet" -> "ব্রেসলেট"
                        "Earrings" -> "দুল"
                        "Pendant" -> "লকেট"
                        else -> item.itemType
                    }
                    Text(
                        text = item.karat + " " + localizedType,
                        fontSize = 10.sp,
                        color = Color(0xFF7D5800),
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (item.isSold) Color(0xFFBA1A1A).copy(alpha = 0.1f) else Color(0xFF1B6B2C).copy(alpha = 0.1f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (item.isSold) "বিক্রিত" else "মজুদ",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (item.isSold) Color(0xFFBA1A1A) else Color(0xFF1B6B2C)
                        )
                    }
                }
                Text(
                    text = item.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F1B16),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "${item.weightGrams} গ্রাম", fontSize = 11.sp, color = Color(0xFF85735E))
                    if (item.valueBdt > 0.0) {
                        Text(text = "৳${String.format("%,.0f", item.valueBdt)}", fontSize = 14.sp, color = Color(0xFF1B6B2C), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ------ MODAL: ADD CUSTOMER ------
@Composable
fun AddCustomerDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, phone: String, email: String, notes: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Register New Client", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_client_name")
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth().testTag("input_client_phone")
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth().testTag("input_client_email")
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Preferred Karat, stone preferences...") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth().testTag("input_client_notes")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onConfirm(name, phone, email, notes) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                enabled = name.isNotBlank()
            ) {
                Text("Register", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// ------ MODAL: ADD INVENTORY ------
@Composable
fun AddInventoryDialog(
    branches: List<com.example.data.Branch>,
    onDismiss: () -> Unit,
    onConfirm: (title: String, type: String, karat: String, weight: Double, value: Double, notes: String, tags: String, valueBdt: Double, paidBdt: Double, dueBdt: Double, base64: String?, branchId: Long?, barcode: String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Ring") }
    var karat by remember { mutableStateOf("18K") }
    var weightStr by remember { mutableStateOf("") }
    var valueStr by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var barcode by remember { mutableStateOf("") }
    var selectedBranchId by remember { mutableStateOf<Long?>(null) }
    var expandedBranches by remember { mutableStateOf(false) }

    // New Tagging & BDT accounting state
    var tags by remember { mutableStateOf("") }
    var valueBdtStr by remember { mutableStateOf("") }
    var paidBdtStr by remember { mutableStateOf("") }

    val computedDueBdt = remember(valueBdtStr, paidBdtStr) {
        val total = valueBdtStr.toDoubleOrNull() ?: 0.0
        val paid = paidBdtStr.toDoubleOrNull() ?: 0.0
        val diff = total - paid
        if (diff < 0) 0.0 else diff
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Jewelry Piece", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title (e.g., Diamond solitaire ring)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_inv_title")
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = type,
                        onValueChange = { type = it },
                        label = { Text("Type") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).padding(end = 4.dp).testTag("input_inv_type")
                    )
                    OutlinedTextField(
                        value = karat,
                        onValueChange = { karat = it },
                        label = { Text("Karat") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).padding(start = 4.dp).testTag("input_inv_karat")
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = weightStr,
                        onValueChange = { weightStr = it },
                        label = { Text("Weight (g)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).padding(end = 4.dp).testTag("input_inv_weight")
                    )
                    OutlinedTextField(
                        value = valueStr,
                        onValueChange = { valueStr = it },
                        label = { Text("USD Value") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).padding(start = 4.dp).testTag("input_inv_value")
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    label = { Text("Tags (comma separated e.g. vintage, 22k)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_inv_tags")
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = barcode,
                    onValueChange = { barcode = it },
                    label = { Text("Barcode / unique ID (Optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_inv_barcode")
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Bangladeshi Taka (BDT) Ledger",
                    modifier = Modifier.padding(bottom = 4.dp),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF7D5800)
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = valueBdtStr,
                        onValueChange = { valueBdtStr = it },
                        label = { Text("Total ৳ BDT") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).padding(end = 4.dp).testTag("input_inv_val_bdt")
                    )
                    OutlinedTextField(
                        value = paidBdtStr,
                        onValueChange = { paidBdtStr = it },
                        label = { Text("Paid ৳ BDT") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).padding(start = 4.dp).testTag("input_inv_paid_bdt")
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Rest / Due Rest: ৳${String.format("%,.1f", computedDueBdt)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = if (computedDueBdt > 0.0) Color(0xFFBA1A1A) else Color(0xFF1B6B2C)
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text("শখা (Store Branch)", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .clickable { expandedBranches = true }
                        .padding(12.dp)
                ) {
                    val branchName = branches.find { it.id == selectedBranchId }?.branchName ?: "শাখা নির্বাচন করুন (Optional)..."
                    Text(text = branchName, style = MaterialTheme.typography.bodyMedium)
                    DropdownMenu(expanded = expandedBranches, onDismissRequest = { expandedBranches = false }) {
                        DropdownMenuItem(text = { Text("None") }, onClick = { selectedBranchId = null; expandedBranches = false })
                        branches.forEach { branch ->
                            DropdownMenuItem(text = { Text(branch.branchName) }, onClick = { selectedBranchId = branch.id; expandedBranches = false })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Setting notes, stone details...") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth().testTag("input_inv_notes")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(
                            title,
                            type,
                            karat,
                            weightStr.toDoubleOrNull() ?: 0.0,
                            valueStr.toDoubleOrNull() ?: 0.0,
                            notes,
                            tags,
                            valueBdtStr.toDoubleOrNull() ?: 0.0,
                            paidBdtStr.toDoubleOrNull() ?: 0.0,
                            computedDueBdt,
                            null,
                            selectedBranchId,
                            barcode.ifBlank { null }
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                enabled = title.isNotBlank()
            ) {
                Text("Store in Vault", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// ------ MODAL: RECORD TRANSACTION / SALE ------
@Composable
fun AddTransactionDialog(
    customers: List<Customer>,
    inventoryItems: List<InventoryItem>,
    onDismiss: () -> Unit,
    onConfirm: (customerId: Long, itemId: Long?, description: String, type: String, amount: Double, amountBdt: Double, paidBdt: Double, dueBdt: Double, notes: String) -> Unit
) {
    var selectedCustomerId by remember { mutableStateOf<Long?>(null) }
    var selectedItemId by remember { mutableStateOf<Long?>(null) }
    var description by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Purchase") } // "Purchase", "Repair", "Custom Order"
    var amountStr by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    // New BDT accounting state
    var valueBdtStr by remember { mutableStateOf("") }
    var paidBdtStr by remember { mutableStateOf("") }

    val computedDueBdt = remember(valueBdtStr, paidBdtStr) {
        val total = valueBdtStr.toDoubleOrNull() ?: 0.0
        val paid = paidBdtStr.toDoubleOrNull() ?: 0.0
        val diff = total - paid
        if (diff < 0) 0.0 else diff
    }

    var expandedClients by remember { mutableStateOf(false) }
    var expandedItems by remember { mutableStateOf(false) }

    val selectedClientName = customers.firstOrNull { it.id == selectedCustomerId }?.name ?: "Select Client..."
    val selectedItemTitle = inventoryItems.firstOrNull { it.id == selectedItemId }?.title ?: "Select Stock (Optional)..."

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Transaction Ledger", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                // Client selector
                Text("Client", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
                        .clickable { expandedClients = true }
                        .padding(12.dp)
                ) {
                    Text(text = selectedClientName, style = MaterialTheme.typography.bodyMedium)
                    DropdownMenu(expanded = expandedClients, onDismissRequest = { expandedClients = false }) {
                        customers.forEach { client ->
                            DropdownMenuItem(
                                text = { Text(client.name) },
                                onClick = {
                                    selectedCustomerId = client.id
                                    expandedClients = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Item selector
                Text("Inventory Catalog Linked Item", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
                        .clickable { expandedItems = true }
                        .padding(12.dp)
                ) {
                    Text(text = selectedItemTitle, style = MaterialTheme.typography.bodyMedium)
                    DropdownMenu(expanded = expandedItems, onDismissRequest = { expandedItems = false }) {
                        DropdownMenuItem(
                            text = { Text("None (Service/Repair)") },
                            onClick = {
                                selectedItemId = null
                                expandedItems = false
                             }
                        )
                        inventoryItems.forEach { item ->
                            DropdownMenuItem(
                                text = { Text("${item.karat} ${item.title} ($${item.estimatedValue})") },
                                onClick = {
                                    selectedItemId = item.id
                                    description = "Purchase of: ${item.title}"
                                    amountStr = item.estimatedValue.toString()
                                    valueBdtStr = item.valueBdt.toString()
                                    paidBdtStr = item.paidBdt.toString()
                                    expandedItems = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Transaction type selector row
                Text("Classification", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Purchase", "Repair", "Custom Order").forEach { serviceType ->
                        ElevatedFilterChip(
                            selected = type == serviceType,
                            onClick = { type = serviceType },
                            label = { Text(serviceType) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Line Description") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_txn_desc")
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Charged Amount (USD)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("input_txn_amount")
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Bangladeshi Taka (BDT) Ledger",
                    modifier = Modifier.padding(bottom = 4.dp),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF7D5800)
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = valueBdtStr,
                        onValueChange = { valueBdtStr = it },
                        label = { Text("Total ৳ BDT") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).padding(end = 4.dp).testTag("input_txn_val_bdt")
                    )
                    OutlinedTextField(
                        value = paidBdtStr,
                        onValueChange = { paidBdtStr = it },
                        label = { Text("Paid ৳ BDT") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).padding(start = 4.dp).testTag("input_txn_paid_bdt")
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Rest / Due Rest: ৳${String.format("%,.1f", computedDueBdt)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = if (computedDueBdt > 0.0) Color(0xFFBA1A1A) else Color(0xFF1B6B2C)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Lapping, material, or service details...") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedCustomerId != null && description.isNotBlank()) {
                        onConfirm(
                            selectedCustomerId!!,
                            selectedItemId,
                            description,
                            type,
                            amountStr.toDoubleOrNull() ?: 0.0,
                            valueBdtStr.toDoubleOrNull() ?: 0.0,
                            paidBdtStr.toDoubleOrNull() ?: 0.0,
                            computedDueBdt,
                            notes
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                enabled = selectedCustomerId != null && description.isNotBlank()
            ) {
                Text("Post Ledger Entry", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun Base64Image(base64Str: String, modifier: Modifier = Modifier, contentDescription: String? = null) {
    val bitmap = remember(base64Str) {
        try {
            val decodedBytes = Base64.decode(base64Str.trim(), Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            null
        }
    }
    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = contentDescription ?: "Jewelry image",
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Photo,
                contentDescription = "No image available",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ------ CUSTOM DIALOGS & INVOICE PRINTERS ------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessConfigDialog(
    config: JewelryViewModel.BusinessConfig,
    onDismiss: () -> Unit,
    onConfirm: (JewelryViewModel.BusinessConfig) -> Unit
) {
    var shopName by remember { mutableStateOf(config.shopName) }
    var ownerName by remember { mutableStateOf(config.ownerName) }
    var phone by remember { mutableStateOf(config.phone) }
    var address by remember { mutableStateOf(config.address) }
    var goldRate22K by remember { mutableStateOf(config.goldRate22K) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, Color(0xFFF0E0CF)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ব্যবসার প্রোফাইল কনফিগার",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7D5800)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                OutlinedTextField(
                    value = shopName,
                    onValueChange = { shopName = it },
                    label = { Text("গহনা দোকানের নাম") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF7D5800),
                        focusedLabelColor = Color(0xFF7D5800)
                    )
                )

                OutlinedTextField(
                    value = ownerName,
                    onValueChange = { ownerName = it },
                    label = { Text("মালিকের নাম") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF7D5800),
                        focusedLabelColor = Color(0xFF7D5800)
                    )
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("মোবাইল নম্বর") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF7D5800),
                        focusedLabelColor = Color(0xFF7D5800)
                    )
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("ঠিকানা / লোকেশন") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF7D5800),
                        focusedLabelColor = Color(0xFF7D5800)
                    )
                )

                OutlinedTextField(
                    value = goldRate22K,
                    onValueChange = { goldRate22K = it },
                    label = { Text("আজকের ২২ক্যারট ভরি রেট (৳)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF7D5800),
                        focusedLabelColor = Color(0xFF7D5800)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        onConfirm(
                            JewelryViewModel.BusinessConfig(
                                shopName = shopName,
                                ownerName = ownerName,
                                phone = phone,
                                address = address,
                                goldRate22K = goldRate22K
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7D5800)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("সংরক্ষণ করুন", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCustomerDialog(
    customer: Customer,
    onDismiss: () -> Unit,
    onConfirm: (Customer) -> Unit,
    onDelete: (Customer) -> Unit
) {
    var name by remember { mutableStateOf(customer.name) }
    var phone by remember { mutableStateOf(customer.phone) }
    var email by remember { mutableStateOf(customer.email) }
    var notes by remember { mutableStateOf(customer.notes) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, Color(0xFFF3EFEA)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "গ্রাহকের তথ্য সংশোধন",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F1B16)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("গ্রাহকের নাম") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("মোবাইল নম্বর") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("ইমেইল ঠিকানা") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("বিশেষ নোট") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { showDeleteConfirm = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFBA1A1A)),
                        border = BorderStroke(1.dp, Color(0xFFBA1A1A))
                    ) {
                        Text("মুছে ফেলুন")
                    }

                    Button(
                        onClick = {
                            onConfirm(
                                customer.copy(
                                    name = name,
                                    phone = phone,
                                    email = email,
                                    notes = notes
                                )
                            )
                        },
                        modifier = Modifier.weight(1.5f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("সংশোধন নিশ্চিত", color = Color.White)
                    }
                }

                if (showDeleteConfirm) {
                    AlertDialog(
                        onDismissRequest = { showDeleteConfirm = false },
                        title = { Text("গ্রাহক মুছে ফেলার সতর্কতা") },
                        text = { Text("আপনি কি নিশ্চিতভাবে এই গ্রাহকের প্রোফাইল ডাটাবেস থেকে ডিলিট করতে চান? এই কাজ পরিবর্তন করা যাবে না।") },
                        confirmButton = {
                            Button(
                                onClick = {
                                    onDelete(customer)
                                    showDeleteConfirm = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBA1A1A))
                            ) {
                                Text("হ্যাঁ, ডিলিট করুন", color = Color.White)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDeleteConfirm = false }) {
                                Text("বাতিল")
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditInventoryDialog(
    item: InventoryItem,
    customers: List<Customer>,
    onDismiss: () -> Unit,
    onConfirm: (InventoryItem) -> Unit,
    onDelete: (InventoryItem) -> Unit
) {
    var title by remember { mutableStateOf(item.title) }
    var itemType by remember { mutableStateOf(item.itemType) }
    var karat by remember { mutableStateOf(item.karat) }
    var weightStr by remember { mutableStateOf(item.weightGrams.toString()) }
    var valueBdtStr by remember { mutableStateOf(item.valueBdt.toString()) }
    var paidBdtStr by remember { mutableStateOf(item.paidBdt.toString()) }
    var dueBdtStr by remember { mutableStateOf(item.dueBdt.toString()) }
    var notes by remember { mutableStateOf(item.notes) }
    var tags by remember { mutableStateOf(item.tags) }
    var isSold by remember { mutableStateOf(item.isSold) }
    var soldToCustomerId by remember { mutableStateOf(item.soldToCustomerId) }

    var expandedTypeDropdown by remember { mutableStateOf(false) }
    var expandedCustomerDropdown by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val itemTypes = listOf("Ring", "Necklace", "Bracelet", "Earrings", "Pendant")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, Color(0xFFF3EFEA)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "গহনা স্টক তথ্য সংশোধন",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F1B16)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("গহনার নাম / বিবরণ") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Item Type Exposed Dropdown Menu
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = itemType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("গহনার ধরন") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { expandedTypeDropdown = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = expandedTypeDropdown,
                        onDismissRequest = { expandedTypeDropdown = false }
                    ) {
                        itemTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    itemType = type
                                    expandedTypeDropdown = false
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = karat,
                        onValueChange = { karat = it },
                        label = { Text("ক্যারেট (যেমন: ২২ক)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = weightStr,
                        onValueChange = { weightStr = it },
                        label = { Text("ওজন (গ্রাম)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = valueBdtStr,
                        onValueChange = { valueBdtStr = it },
                        label = { Text("মূল্য (৳ BDT)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = paidBdtStr,
                        onValueChange = { paidBdtStr = it },
                        label = { Text("পরিশোধ (৳)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = dueBdtStr,
                    onValueChange = { dueBdtStr = it },
                    label = { Text("অবশিষ্ট বকেয়া দেনা (৳)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    label = { Text("ট্যাগসমূহ (কমা দিয়ে লিখুন)") },
                    placeholder = { Text("যেমন: gold, design, earrings") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("অতিরিক্ত নোট") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Status switches (Is Sold and Patron allocation)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("বিক্রি হয়ে গেছে?", fontWeight = FontWeight.SemiBold)
                    Switch(
                        checked = isSold,
                        onCheckedChange = { isSold = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF1B6B2C))
                    )
                }

                if (isSold) {
                    // Dropdown list for sold customer allocation
                    val selectedBuyerName = customers.firstOrNull { it.id == soldToCustomerId }?.name ?: "ক্রেতা নির্বাচন করুন"
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedBuyerName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("ক্রেতা গ্রাহক বরাদ্দ") },
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                IconButton(onClick = { expandedCustomerDropdown = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            }
                        )
                        DropdownMenu(
                            expanded = expandedCustomerDropdown,
                            onDismissRequest = { expandedCustomerDropdown = false }
                        ) {
                            customers.forEach { cust ->
                                DropdownMenuItem(
                                    text = { Text(cust.name) },
                                    onClick = {
                                        soldToCustomerId = cust.id
                                        expandedCustomerDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { showDeleteConfirm = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFBA1A1A)),
                        border = BorderStroke(1.dp, Color(0xFFBA1A1A))
                    ) {
                        Text("ডিলিট")
                    }

                    Button(
                        onClick = {
                            val w = weightStr.toDoubleOrNull() ?: item.weightGrams
                            val valB = valueBdtStr.toDoubleOrNull() ?: item.valueBdt
                            val paidB = paidBdtStr.toDoubleOrNull() ?: item.paidBdt
                            val dueB = dueBdtStr.toDoubleOrNull() ?: item.dueBdt
                            onConfirm(
                                item.copy(
                                    title = title,
                                    itemType = itemType,
                                    karat = karat,
                                    weightGrams = w,
                                    valueBdt = valB,
                                    paidBdt = paidB,
                                    dueBdt = dueB,
                                    notes = notes,
                                    tags = tags,
                                    isSold = isSold,
                                    soldToCustomerId = if (isSold) soldToCustomerId else null
                                )
                            )
                        },
                        modifier = Modifier.weight(1.5f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("সংরক্ষণ", color = Color.White)
                    }
                }

                if (showDeleteConfirm) {
                    AlertDialog(
                        onDismissRequest = { showDeleteConfirm = false },
                        title = { Text("গহনা ডিলিট করার সতর্কতা") },
                        text = { Text("আপনি কি নিশ্চিতভাবে এই গহনা পিসটি স্টক ডাটাবেস থেকে মুছে ফেলতে চান? এটি মুছে ফেললে তা আর ফিরে পাওয়া যাবে না।") },
                        confirmButton = {
                            Button(
                                onClick = {
                                    onDelete(item)
                                    showDeleteConfirm = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBA1A1A))
                            ) {
                                Text("হ্যাঁ, মুছে ফেলুন", color = Color.White)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDeleteConfirm = false }) {
                                Text("বাতিল")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun InvoicePreviewDialog(
    txn: Transaction,
    customers: List<Customer>,
    businessConfig: JewelryViewModel.BusinessConfig,
    onDismiss: () -> Unit,
    onPayDue: (Transaction, Double, String) -> Unit
) {
    val buyer = remember(customers, txn.customerId) {
        customers.firstOrNull { it.id == txn.customerId }
    }
    val buyerName = buyer?.name ?: "সম্মানিত ক্রেতা"
    val buyerPhone = buyer?.phone ?: "০১৭XXXXXXXX"
    val buyerAddress = buyer?.notes ?: "বাংলাদেশ"

    val dialogDate = remember(txn.date) {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(txn.date))
    }
    val memoSerial = remember(txn.id) {
        "SS-${1000 + txn.id}"
    }

    var showPayDueDialog by remember { mutableStateOf(false) }
    var payAmountStr by remember { mutableStateOf("") }
    var payNotesStr by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(2.dp, Color(0xFFE5A93B)), // Gold-themed border
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(14.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Outer double line visual frame
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF7D5800).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Header metadata
                        Text(
                            text = businessConfig.shopName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF7D5800),
                            fontSize = 24.sp,
                            fontFamily = FontFamily.Serif
                        )
                        Text(
                            text = "ডিজিটাল ক্যাশ মেমো চালান (রসিদ)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF504539)
                        )
                        Divider(
                            color = Color(0xFFEBF0E6),
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Text(
                            text = "ঠিকানা: ${businessConfig.address}",
                            fontSize = 11.sp,
                            color = Color(0xFF85735E)
                        )
                        Text(
                            text = "মোবাইল: ${businessConfig.phone}",
                            fontSize = 11.sp,
                            color = Color(0xFF85735E)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Customer Metadata block
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("মেমো নং: #$memoSerial", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("আইডি নং: ${txn.customerId}", fontSize = 10.sp, color = Color(0xFF85735E))
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("তারিখ: $dialogDate", fontSize = 11.sp)
                                Text("সময়: রিয়েল-টাইম", fontSize = 10.sp, color = Color(0xFF85735E))
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFAF6F0), RoundedCornerShape(8.dp))
                                .border(0.5.dp, Color(0xFFEAE2D9), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text("খদ্দেরের বিবরণী:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7D5800))
                                Text("নাম: $buyerName", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("মোবাইল: $buyerPhone", fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Table representing the billable transaction description
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFFDDB3))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("বিবরণ", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f))
                            Text("লেনদেন", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                            Text("মোট (৳)", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                        }

                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = txn.itemDescription,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(2f)
                            )
                            val localizedTxType = if (txn.transactionType == "Purchase" || txn.transactionType == "বিক্রয়") "বিক্রয়" else txn.transactionType
                            Text(
                                text = localizedTxType,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "৳ ${String.format("%,.0f", txn.amountBdt)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.End
                            )
                        }

                        Divider(color = Color(0xFFEAE2D9))

                        // Cost totals summary drawer
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("সর্বমোট মূল্য:", fontSize = 12.sp)
                                Text("৳ ${String.format("%,.0f", txn.amountBdt)} BDT", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("পরিশোধিত টাকা:", fontSize = 12.sp, color = Color(0xFF1B6B2C))
                                Text("৳ ${String.format("%,.0f", txn.paidBdt)}", fontSize = 12.sp, color = Color(0xFF1B6B2C), fontWeight = FontWeight.Bold)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("অবशिष्ट বকেয়া দেনা:", fontSize = 12.sp, color = Color(0xFFBA1A1A))
                                Text("৳ ${String.format("%,.0f", txn.dueBdt)}", fontSize = 12.sp, color = Color(0xFFBA1A1A), fontWeight = FontWeight.Bold)
                            }
                        }

                        Divider(color = Color(0xFFEAE2D9))

                        // Terms and Conditions Section
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                        ) {
                            Text(
                                "শর্তাবলী:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF7D5800)
                            )
                            Text(
                                "১. বিক্রিত মাল ফেরত নেওয়া হয় না।\n২. গহনার গুণগত মানের গ্যারান্টি আমরা প্রদান করি।\n৩. আমাদের দোকানে আসার জন্য আপনাকে অশেষ ধন্যবাদ।",
                                fontSize = 9.sp,
                                color = Color.Gray,
                                lineHeight = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Signature Area
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Divider(thickness = 1.dp, color = Color.Black, modifier = Modifier.width(80.dp).padding(bottom = 2.dp))
                                Text("ক্রেতার স্বাক্ষর", fontSize = 9.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Divider(thickness = 1.dp, color = Color.Black, modifier = Modifier.width(80.dp).padding(bottom = 2.dp))
                                Text("বিক্রেতার স্বাক্ষর", fontSize = 9.sp)
                            }
                        }

                        // Interaction Buttons (PDF / Share)
                        val context = LocalContext.current
                        Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    val file = com.example.util.PdfGenerator.generateInvoicePdf(
                                        context = context,
                                        transaction = txn,
                                        customer = buyer,
                                        businessConfig = businessConfig
                                    )
                                    if (file != null) {
                                        val uri = androidx.core.content.FileProvider.getUriForFile(context, "com.example.fileprovider", file)
                                        val intent = Intent(Intent.ACTION_VIEW).apply {
                                            setDataAndType(uri, "application/pdf")
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                        context.startActivity(Intent.createChooser(intent, "বিবরণীটি খুলুন"))
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7D5800)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("PDF মেমো", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    val file = com.example.util.PdfGenerator.generateInvoicePdf(
                                        context = context,
                                        transaction = txn,
                                        customer = buyer,
                                        businessConfig = businessConfig
                                    )
                                    if (file != null) {
                                        val uri = androidx.core.content.FileProvider.getUriForFile(context, "com.example.fileprovider", file)
                                        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                            type = "application/pdf"
                                            putExtra(android.content.Intent.EXTRA_STREAM, uri)
                                            addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                        context.startActivity(android.content.Intent.createChooser(intent, "মেমো শেয়ার করুন"))
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF504539)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("শেয়ার ক্যাশ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Stylized Dual Signatures representing authentic cash memo layout
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Divider(color = Color(0xFF85735E), thickness = 0.5.dp, modifier = Modifier.width(80.dp))
                                Text("গ্রহীতার স্বাক্ষর", fontSize = 9.sp, color = Color(0xFF85735E))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Divider(color = Color(0xFF7D5800), thickness = 0.5.dp, modifier = Modifier.width(80.dp))
                                Text("স্বত্বাধিকারী স্বাক্ষর", fontSize = 9.sp, color = Color(0xFF7D5800), fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "বিক্রিত মাল ফেরত নেওয়া হয় না। স্বর্ণালি শিল্পালয় পাশে থাকার জন্য ধন্যবাদ।",
                            fontSize = 8.sp,
                            color = Color(0xFF85735E)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (txn.dueBdt > 0.0) {
                    Button(
                        onClick = {
                            payAmountStr = String.format("%.0f", txn.dueBdt)
                            payNotesStr = ""
                            showPayDueDialog = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B6B2C)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Payments, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("বকেয়া কিস্তি পরিশোধ করুন (৳ ${String.format("%,.0f", txn.dueBdt)})", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7D5800)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("মেমো রশিদের প্রিন্ট বন্ধ করুন", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showPayDueDialog) {
        AlertDialog(
            onDismissRequest = { showPayDueDialog = false },
            title = {
                Text(
                    text = "বকেয়া কিস্তি পরিশোধ ফরম",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF7D5800)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "গ্রাহক: $buyerName\nমেমো নং: #$memoSerial\nমোট বকেয়া দেনা: ৳ ${String.format("%,.0f", txn.dueBdt)} টাকা",
                        fontSize = 12.sp,
                        color = Color(0xFF504539),
                        lineHeight = 16.sp
                    )
                    OutlinedTextField(
                        value = payAmountStr,
                        onValueChange = { payAmountStr = it },
                        label = { Text("জমা প্রদানের পরিমাণ (৳ BDT)") },
                        placeholder = { Text("যেমন: 5000") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF7D5800),
                            unfocusedBorderColor = Color(0xFFE5D5C5)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = payNotesStr,
                        onValueChange = { payNotesStr = it },
                        label = { Text("মন্তব্য / পরিশোধের মাধ্যম") },
                        placeholder = { Text("নগদ / বিকাশ / ব্যাংক রশিদ") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF7D5800),
                            unfocusedBorderColor = Color(0xFFE5D5C5)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = payAmountStr.toDoubleOrNull()
                        if (amount != null && amount > 0 && amount <= txn.dueBdt) {
                            onPayDue(txn, amount, payNotesStr)
                            showPayDueDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B6B2C)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("নিশ্চিত ও সংরক্ষণ", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPayDueDialog = false }) {
                    Text("বাতিল করুন", color = Color(0xFFBA1A1A), fontSize = 12.sp)
                }
            }
        )
    }
}

@Composable
fun GoldCalculatorWidget(businessConfig: JewelryViewModel.BusinessConfig) {
    var expanded by remember { mutableStateOf(false) }

    var bhariInput by remember { mutableStateOf("") }
    var annaInput by remember { mutableStateOf("") }
    var rattiInput by remember { mutableStateOf("") }
    var pointInput by remember { mutableStateOf("") }
    var selectedKarat by remember { mutableStateOf("22K") }

    val default22KRateStr = businessConfig.goldRate22K
    val clean22KRate = remember(default22KRateStr) {
        val bengaliToEnglish = mapOf(
            '০' to '0', '১' to '1', '২' to '2', '৩' to '3', '৪' to '4',
            '৫' to '5', '৬' to '6', '৭' to '7', '৮' to '8', '৯' to '9'
        )
        val engDigits = default22KRateStr.map { bengaliToEnglish[it] ?: it }.joinToString("")
        engDigits.filter { it.isDigit() }.toDoubleOrNull() ?: 115000.0
    }

    val baseRateForKarat = remember(clean22KRate, selectedKarat) {
        when (selectedKarat) {
            "24K" -> clean22KRate * (24.0 / 22.0)
            "22K" -> clean22KRate
            "21K" -> clean22KRate * (21.0 / 22.0)
            "18K" -> clean22KRate * (18.0 / 22.0)
            else -> clean22KRate
        }
    }

    var rateInput by remember(baseRateForKarat) { mutableStateOf(String.format("%.0f", baseRateForKarat)) }
    var makingChargeInput by remember { mutableStateOf("4000") }
    var vatPercentageInput by remember { mutableStateOf("5") }

    val bhari = bhariInput.toDoubleOrNull() ?: 0.0
    val anna = annaInput.toDoubleOrNull() ?: 0.0
    val ratti = rattiInput.toDoubleOrNull() ?: 0.0
    val point = pointInput.toDoubleOrNull() ?: 0.0

    val totalBhari = bhari + (anna / 16.0) + (ratti / 96.0) + (point / 960.0)
    val totalGrams = totalBhari * 11.664

    val calculatedRate = rateInput.toDoubleOrNull() ?: baseRateForKarat
    val goldValue = totalBhari * calculatedRate

    val makingCharge = makingChargeInput.toDoubleOrNull() ?: 0.0
    val subTotal = goldValue + makingCharge

    val vatPercent = vatPercentageInput.toDoubleOrNull() ?: 5.0
    val vatAmount = subTotal * (vatPercent / 100.0)
    val grandTotal = subTotal + vatAmount

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDF9)),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.5.dp, Color(0xFFE5A93B).copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = "Calculator",
                        tint = Color(0xFF7D5800),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "স্বর্ণের ভরি-আনা-রতি মূল্য হিসাব ক্যালকুলেটর",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF291800)
                        )
                        Text(
                            text = if (expanded) "ওজন ও মজুরি দিন" else "ভরি, আনা, রতি ও পয়েন্ট রূপান্তর করে মূল্য হিসাব করুন",
                            fontSize = 11.sp,
                            color = Color(0xFF85735E)
                        )
                    }
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Color(0xFF7D5800)
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(14.dp))
                Divider(color = Color(0xFFF3EFEA))
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "সোনার ক্যারেট মান (Purity):",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF504539),
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val karats = listOf("24K", "22K", "21K", "18K")
                    karats.forEach { k ->
                        val isSelected = selectedKarat == k
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Color(0xFFFFDDB3) else Color(0xFFF5EFE6))
                                .border(1.dp, if (isSelected) Color(0xFF7D5800) else Color(0xFFEAE2D9), RoundedCornerShape(8.dp))
                                .clickable { selectedKarat = k }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = k,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color(0xFF291800) else Color(0xFF504539)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "ওজন হিসাব (Weight Units):",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF504539),
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = bhariInput,
                        onValueChange = { bhariInput = it },
                        label = { Text("ভরি (Bhari)", fontSize = 10.sp) },
                        placeholder = { Text("0") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF7D5800),
                            unfocusedBorderColor = Color(0xFFE5D5C5)
                        ),
                        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                    )
                    OutlinedTextField(
                        value = annaInput,
                        onValueChange = { annaInput = it },
                        label = { Text("আনা (Anna)", fontSize = 10.sp) },
                        placeholder = { Text("0") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF7D5800),
                            unfocusedBorderColor = Color(0xFFE5D5C5)
                        ),
                        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                    )
                    OutlinedTextField(
                        value = rattiInput,
                        onValueChange = { rattiInput = it },
                        label = { Text("রতি (Ratti)", fontSize = 10.sp) },
                        placeholder = { Text("0") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF7D5800),
                            unfocusedBorderColor = Color(0xFFE5D5C5)
                        ),
                        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                    )
                    OutlinedTextField(
                        value = pointInput,
                        onValueChange = { pointInput = it },
                        label = { Text("পয়েন্ট (Pt)", fontSize = 10.sp) },
                        placeholder = { Text("0") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF7D5800),
                            unfocusedBorderColor = Color(0xFFE5D5C5)
                        ),
                        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = rateInput,
                        onValueChange = { rateInput = it },
                        label = { Text("ভরি রেট (৳/ভরি)", fontSize = 10.sp) },
                        modifier = Modifier.weight(1.2f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF7D5800),
                            unfocusedBorderColor = Color(0xFFE5D5C5)
                        ),
                        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                    )
                    OutlinedTextField(
                        value = makingChargeInput,
                        onValueChange = { makingChargeInput = it },
                        label = { Text("মজুরি (৳ Flat)", fontSize = 10.sp) },
                        modifier = Modifier.weight(0.9f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF7D5800),
                            unfocusedBorderColor = Color(0xFFE5D5C5)
                        ),
                        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                    )
                    OutlinedTextField(
                        value = vatPercentageInput,
                        onValueChange = { vatPercentageInput = it },
                        label = { Text("ভ্যাট (% VAT)", fontSize = 10.sp) },
                        modifier = Modifier.weight(0.7f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF7D5800),
                            unfocusedBorderColor = Color(0xFFE5D5C5)
                        ),
                        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFAF6F0), RoundedCornerShape(12.dp))
                        .border(0.5.dp, Color(0xFFEAE2D9), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("মোট ওজন ভর:", fontSize = 12.sp, color = Color(0xFF504539))
                            Text(
                                text = String.format("%.4f ভরি (~%.3f গ্রাম)", totalBhari, totalGrams),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F1B16)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("খাদ সোনা মূল্য:", fontSize = 12.sp, color = Color(0xFF504539))
                            Text(
                                text = "৳ ${String.format("%,.0f", goldValue)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1F1B16)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("মজুরি (Making):", fontSize = 12.sp, color = Color(0xFF504539))
                            Text(
                                text = "৳ ${String.format("%,.0f", makingCharge)}",
                                fontSize = 12.sp,
                                color = Color(0xFF1F1B16)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("ভ্যাট (VAT $vatPercentageInput%):", fontSize = 12.sp, color = Color(0xFF504539))
                            Text(
                                text = "৳ ${String.format("%,.0f", vatAmount)}",
                                fontSize = 12.sp,
                                color = Color(0xFF1F1B16)
                            )
                        }

                        Divider(color = Color(0xFFEAE2D9), thickness = 0.5.dp, modifier = Modifier.padding(vertical = 4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("আনুমানিক মোট বিক্রয় মূল্য:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7D5800))
                            Text(
                                text = "৳ ${String.format("%,.0f", grandTotal)} BDT",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF1F1B16)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {
                        bhariInput = ""
                        annaInput = ""
                        rattiInput = ""
                        pointInput = ""
                    }) {
                        Text("ক্যালকুলেটর পরিষ্কার করুন", fontSize = 11.sp, color = Color(0xFFBA1A1A))
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryDistributionChart(inventoryItems: List<InventoryItem>) {
    val unsold = remember(inventoryItems) { inventoryItems.filter { !it.isSold } }
    val totalUnsold = unsold.size

    if (totalUnsold == 0) return

    val categoryCounts = remember(unsold) {
        unsold.groupBy { it.itemType }.mapValues { it.value.size }
    }

    val ringCount = categoryCounts["Ring"] ?: 0
    val necklaceCount = categoryCounts["Necklace"] ?: 0
    val braceletCount = categoryCounts["Bracelet"] ?: 0
    val earringsCount = categoryCounts["Earrings"] ?: 0
    val pendantCount = categoryCounts["Pendant"] ?: 0
    val othersCount = totalUnsold - (ringCount + necklaceCount + braceletCount + earringsCount + pendantCount)

    val ringColor = Color(0xFF7D5800)
    val necklaceColor = Color(0xFFC29026)
    val braceletColor = Color(0xFFE5A93B)
    val earringsColor = Color(0xFFFED285)
    val pendantColor = Color(0xFFFFECCC)
    val othersColor = Color(0xFFFAF1DF)

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color(0xFFF3EFEA)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "📊 স্টক গহনা ক্যাটাগরি বিশ্লেষণ (অনুপাত)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F1B16)
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5EFE6))
            ) {
                if (ringCount > 0) {
                    Box(modifier = Modifier.weight(ringCount.toFloat()).fillMaxHeight().background(ringColor))
                }
                if (necklaceCount > 0) {
                    Box(modifier = Modifier.weight(necklaceCount.toFloat()).fillMaxHeight().background(necklaceColor))
                }
                if (braceletCount > 0) {
                    Box(modifier = Modifier.weight(braceletCount.toFloat()).fillMaxHeight().background(braceletColor))
                }
                if (earringsCount > 0) {
                    Box(modifier = Modifier.weight(earringsCount.toFloat()).fillMaxHeight().background(earringsColor))
                }
                if (pendantCount > 0) {
                    Box(modifier = Modifier.weight(pendantCount.toFloat()).fillMaxHeight().background(pendantColor))
                }
                if (othersCount > 0) {
                    Box(modifier = Modifier.weight(othersCount.toFloat()).fillMaxHeight().background(othersColor))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(
                    Triple("আংটি (Ring)", ringCount, ringColor),
                    Triple("নেকলেস (Necklace)", necklaceCount, necklaceColor),
                    Triple("চুড়ি/বালা (Bracelet)", braceletCount, braceletColor),
                    Triple("কানের দুল (Earrings)", earringsCount, earringsColor),
                    Triple("লকেট (Pendant)", pendantCount, pendantColor),
                    Triple("অন্যান্য গহনা", othersCount, othersColor)
                ).chunked(2).forEach { rowItems ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        rowItems.forEach { (label, count, color) ->
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "$label: $count টি",
                                    fontSize = 11.sp,
                                    color = Color(0xFF504539),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ManagementTabContent(
    viewModel: JewelryViewModel,
    onAddSupplier: () -> Unit,
    onAddArtisan: () -> Unit,
    onAddEmployee: () -> Unit,
    onAddBranch: () -> Unit
) {
    var subTab by remember { mutableStateOf("suppliers") } // "suppliers", "artisans", "employees", "branches"

    val suppliers by viewModel.suppliers.collectAsState()
    val artisans by viewModel.artisans.collectAsState()
    val employees by viewModel.employees.collectAsState()
    val branches by viewModel.branches.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ব্যবসায়িক ব্যবস্থাপনা",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF7D5800)
            )
            IconButton(
                onClick = {
                    when(subTab) {
                        "suppliers" -> onAddSupplier()
                        "artisans" -> onAddArtisan()
                        "employees" -> onAddEmployee()
                        "branches" -> onAddBranch()
                    }
                },
                modifier = Modifier.background(Color(0xFFFAF2E6), CircleShape)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Item", tint = Color(0xFF7D5800))
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        ScrollableTabRow(
            selectedTabIndex = when(subTab) {
                "suppliers" -> 0
                "artisans" -> 1
                "employees" -> 2
                "branches" -> 3
                else -> 0
            },
            containerColor = Color.Transparent,
            contentColor = Color(0xFF7D5800),
            edgePadding = 0.dp,
            divider = {}
        ) {
            Tab(selected = subTab == "suppliers", onClick = { subTab = "suppliers" }) {
                Text("সাপ্লায়ার (মহাজন)", modifier = Modifier.padding(12.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Tab(selected = subTab == "artisans", onClick = { subTab = "artisans" }) {
                Text("কারিগর", modifier = Modifier.padding(12.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Tab(selected = subTab == "employees", onClick = { subTab = "employees" }) {
                Text("কর্মচারী", modifier = Modifier.padding(12.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Tab(selected = subTab == "branches", onClick = { subTab = "branches" }) {
                Text("শাখা (শোরুম)", modifier = Modifier.padding(12.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            when (subTab) {
                "suppliers" -> {
                    items(suppliers) { SupplierItemCard(it) }
                    if (suppliers.isEmpty()) item { EmptyState("কোনো সাপ্লায়ার পাওয়া যায়নি") }
                }
                "artisans" -> {
                    items(artisans) { ArtisanItemCard(it) }
                    if (artisans.isEmpty()) item { EmptyState("কোনো কারিগর পাওয়া যায়নি") }
                }
                "employees" -> {
                    items(employees) { EmployeeItemCard(it) }
                    if (employees.isEmpty()) item { EmptyState("কোনো কর্মচারী পাওয়া যায়নি") }
                }
                "branches" -> {
                    items(branches) { BranchItemCard(it) }
                    if (branches.isEmpty()) item { EmptyState("কোনো শাখা পাওয়া যায়নি") }
                }
            }
        }
    }
}

@Composable
fun PnLSummaryCard(viewModel: JewelryViewModel) {
    val pnlData = viewModel.getProfitLossData()
    val income = pnlData.find { it.first == "Income" }?.second ?: 0.0
    val expense = pnlData.find { it.first == "Expense" }?.second ?: 0.0
    val profit = (income - expense).coerceAtLeast(0.0)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF3EFEA)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "চলতি মাসের আয়-ব্যয় চিত্র (P&L)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF7D5800))
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth().height(120.dp), verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.SpaceAround) {
                // Simple Bar Chart via Box
                PnLBar("আয়", income, Color(0xFF1B6B2C), income.coerceAtLeast(expense))
                PnLBar("ব্যয়", expense, Color(0xFFBA1A1A), income.coerceAtLeast(expense))
                PnLBar("লাভ", profit, Color(0xFFE6AC00), income.coerceAtLeast(expense))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color(0xFFF3EFEA))
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "নিট লাভ: ৳ ${String.format("%,.0f", profit)}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF1B6B2C))
                Text(text = "মাসের খতিয়ান", fontSize = 10.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun PnLBar(label: String, value: Double, color: Color, maxValue: Double) {
    val heightFactor = if (maxValue > 0) (value / maxValue).toFloat() else 0f
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "৳${String.format("%,.0f", value)}", fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .width(40.dp)
                .fillMaxHeight(heightFactor.coerceIn(0.1f, 1f))
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, fontSize = 10.sp, color = Color.Gray)
    }
}

@Composable
fun FinanceTabContent(
    viewModel: JewelryViewModel,
    onAddBankAccount: () -> Unit,
    onAddBusinessAccount: () -> Unit
) {
    val context = LocalContext.current
    val inventoryItems by viewModel.inventoryItems.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val bankAccounts by viewModel.bankAccounts.collectAsState()
    val businessAccounts by viewModel.businessAccounts.collectAsState()
    val customers by viewModel.customers.collectAsState()
    
    var subTab by remember { mutableStateOf("banking") } // "banking", "accounts", "reports"

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "আর্থিক হিসাব-নিকাশ",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF7D5800)
            )
            IconButton(
                onClick = {
                    when(subTab) {
                        "banking" -> onAddBankAccount()
                        "accounts" -> onAddBusinessAccount()
                    }
                },
                modifier = Modifier.background(Color(0xFFFAF2E6), CircleShape)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Item", tint = Color(0xFF7D5800))
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        TabRow(
            selectedTabIndex = when(subTab) {
                "banking" -> 0
                "accounts" -> 1
                "reports" -> 2
                else -> 0
            },
            containerColor = Color.Transparent,
            contentColor = Color(0xFF7D5800),
            divider = {}
        ) {
            Tab(selected = subTab == "banking", onClick = { subTab = "banking" }) {
                Text("ব্যাংক একাউন্ট", modifier = Modifier.padding(12.dp), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Tab(selected = subTab == "accounts", onClick = { subTab = "accounts" }) {
                Text("আয়-ব্যয় একাউন্ট", modifier = Modifier.padding(12.dp), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Tab(selected = subTab == "reports", onClick = { subTab = "reports" }) {
                Text("রিপোর্ট ও ব্যাকআপ", modifier = Modifier.padding(12.dp), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            when (subTab) {
                "banking" -> {
                    items(bankAccounts) { BankAccountCard(it) }
                    if (bankAccounts.isEmpty()) item { EmptyState("কোনো ব্যাংক একাউন্ট পাওয়া যায়নি") }
                }
                "accounts" -> {
                    items(businessAccounts) { BusinessAccountCard(it) }
                    if (businessAccounts.isEmpty()) item { EmptyState("কোনো আয়-ব্যয় একাউন্ট পাওয়া যায়নি") }
                }
                "reports" -> {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFF3EFEA))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = "এক্সপোর্ট ও ব্যাকআপ (CSV)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "আপনার সমস্ত ইনভেন্টরি এবং লেনদেন ডাটা ব্যাকআপ হিসেবে ডাউনলোড করুন।", fontSize = 12.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {
                                            val file = com.example.util.CsvExporter.exportInventoryToCsv(context, inventoryItems)
                                            if (file != null) {
                                                val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                                                val intent = Intent(Intent.ACTION_SEND).apply {
                                                    type = "text/csv"
                                                    putExtra(Intent.EXTRA_STREAM, uri)
                                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                                }
                                                context.startActivity(Intent.createChooser(intent, "Inventory Export"))
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("স্টক এক্সপোর্ট", fontSize = 12.sp)
                                    }
                                    Button(
                                        onClick = {
                                            val file = com.example.util.CsvExporter.exportTransactionsToCsv(context, transactions)
                                            if (file != null) {
                                                val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                                                val intent = Intent(Intent.ACTION_SEND).apply {
                                                    type = "text/csv"
                                                    putExtra(Intent.EXTRA_STREAM, uri)
                                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                                }
                                                context.startActivity(Intent.createChooser(intent, "Transactions Export"))
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("লেনদেন এক্সপোর্ট", fontSize = 12.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        val file = com.example.util.CsvExporter.exportCustomersToCsv(context, customers)
                                        if (file != null) {
                                            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                                            val intent = Intent(Intent.ACTION_SEND).apply {
                                                type = "text/csv"
                                                putExtra(Intent.EXTRA_STREAM, uri)
                                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                            }
                                            context.startActivity(Intent.createChooser(intent, "Customers Export"))
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF504539))
                                ) {
                                    Text("গ্রাহক তথ্য এক্সপোর্ট", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SupplierItemCard(supplier: com.example.data.Supplier) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(0.5.dp, Color(0xFFEAE2D9)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = supplier.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = "যোগাযোগ: ${supplier.contact}", fontSize = 12.sp, color = Color.Gray)
            Text(text = "ঠিকানা: ${supplier.address}", fontSize = 12.sp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "পরিশোধিত: ৳ ${supplier.totalPaidBdt}", fontSize = 11.sp, color = Color(0xFF1B6B2C))
                Text(text = "বকেয়া দেনা: ৳ ${supplier.totalDueBdt}", fontSize = 11.sp, color = Color.Red, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ArtisanItemCard(artisan: com.example.data.Artisan) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(0.5.dp, Color(0xFFEAE2D9)), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = artisan.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = "যোগাযোগ: ${artisan.contact}", fontSize = 12.sp, color = Color.Gray)
            Text(text = "বিশেষত্ব: ${artisan.specialty}", fontSize = 12.sp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "মজুদ সোনা: ${artisan.goldBalanceGrams} গ্রাম", fontSize = 11.sp, color = Color(0xFF7D5800))
                Text(text = "বকেয়া মজুরি: ৳ ${artisan.wageDueBdt}", fontSize = 11.sp, color = Color.Red, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun EmployeeItemCard(employee: com.example.data.Employee) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(0.5.dp, Color(0xFFEAE2D9)), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = employee.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = "পদবী: ${employee.role}", fontSize = 12.sp, color = Color(0xFF7D5800))
            Text(text = "ফোন: ${employee.phone}", fontSize = 12.sp, color = Color.Gray)
            Text(text = "বেতন: ৳ ${employee.salary}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun BranchItemCard(branch: com.example.data.Branch) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(0.5.dp, Color(0xFFEAE2D9)), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = branch.branchName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = "ঠিকানা: ${branch.location}", fontSize = 12.sp, color = Color.Gray)
            Text(text = "ফোন: ${branch.phone}", fontSize = 12.sp)
            if (branch.isMainBranch) {
                Text(text = "প্রধান শাখা", fontSize = 10.sp, color = Color(0xFF7D5800), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun BankAccountCard(bank: com.example.data.BankAccount) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(0.5.dp, Color(0xFFEAE2D9)), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = bank.bankName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = "হিসাব নং: ${bank.accountNo}", fontSize = 12.sp, color = Color.Gray)
            Text(text = "বর্তমান একাউন্ট ব্যালেন্স: ৳ ${bank.balanceBdt}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B6B2C))
        }
    }
}

@Composable
fun BusinessAccountCard(account: com.example.data.BusinessAccount) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(0.5.dp, Color(0xFFEAE2D9)), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = account.category, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                val color = if (account.type == "Income") Color(0xFF1B6B2C) else Color.Red
                Text(text = "৳ ${account.amountBdt}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
            }
            Text(text = "ধরন: ${if (account.type == "Income") "আয়" else "ব্যয়"}", fontSize = 11.sp, color = Color.Gray)
            Text(text = "মন্তব্য: ${account.notes}", fontSize = 11.sp)
            val dateStr = remember(account.date) { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(account.date)) }
            Text(text = "তারিখ: $dateStr", fontSize = 10.sp, color = Color.LightGray)
        }
    }
}

@Composable
fun EmptyState(message: String) {
    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Info, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = message, color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalSearchDialog(
    viewModel: JewelryViewModel,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val aiInventory by viewModel.aiFilteredInventoryItems.collectAsState()
    val aiCustomers by viewModel.aiFilteredCustomers.collectAsState()
    val message by viewModel.globalSearchMessage.collectAsState()
    val searchState by viewModel.aiInventorySearchState.collectAsState()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("সুপার সার্চ (এআই)", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = null) }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("যেকোনো কিছু লিখে খুঁজুন (যেমন: ২২ ক্যারেটের দুল, বকেয়া কাস্টমার)") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { viewModel.searchGlobalSemantic(query) }) {
                            Icon(Icons.Default.Search, contentDescription = null)
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Search)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                if (searchState is AIInventorySearchState.Loading) {
                    Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(30.dp))
                    }
                }
                
                if (message.isNotEmpty()) {
                    Text(text = message, fontSize = 13.sp, color = Color(0xFF7D5800), fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
                    Divider()
                }
                
                LazyColumn(modifier = Modifier.weight(1f)) {
                    if (aiCustomers != null && aiCustomers!!.isNotEmpty()) {
                        item { Text("গ্রাহকসমূহ (${aiCustomers!!.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(vertical = 8.dp)) }
                        items(aiCustomers!!) { cust ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(0.5.dp, Color.LightGray)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(cust.name, fontWeight = FontWeight.Bold)
                                    Text(cust.phone, fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                    
                    if (aiInventory != null && aiInventory!!.isNotEmpty()) {
                        item { Text("ইনভেন্টরি (${aiInventory!!.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)) }
                        items(aiInventory!!) { item ->
                            InventoryItemCard(item = item, onClick = {})
                        }
                    }
                    
                    if (searchState is AIInventorySearchState.Success && (aiInventory.isNullOrEmpty() && aiCustomers.isNullOrEmpty())) {
                        item { EmptyState("কোনো তথ্য পাওয়া যায়নি") }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDialog(viewModel: JewelryViewModel, onDismiss: () -> Unit) {
    val isDark by viewModel.isDarkMode.collectAsState()
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    "সেটিং এবং থিম",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("ডার্ক মোড (Dark Mode)")
                    }
                    Switch(
                        checked = isDark,
                        onCheckedChange = { viewModel.toggleTheme() }
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "ডেটা ব্যাকআপ এবং ম্যানেজমেন্ট",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.exportBackup() },
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, Color(0xFF7D5800)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color(0xFF7D5800), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ব্যাকআপ নিন", fontSize = 11.sp, color = Color(0xFF7D5800))
                    }
                    OutlinedButton(
                        onClick = { /* Restore Logic Placeholder */ },
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, Color.Gray),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ব্রাউজ ব্যাকআপ", fontSize = 11.sp, color = Color.Gray)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    "এপ্লিকেশন তথ্য",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "স্বর্ণালি শিল্পালয় স্মার্ট বিজনেস ম্যানেজমেন্ট\nভার্সন: ৩.১.০\nডেভেলপড বাই এআই স্টুডিও",
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7D5800))
                ) {
                    Text("বন্ধ করুন", color = Color.White)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIAssistantChatSheet(viewModel: JewelryViewModel, onDismiss: () -> Unit) {
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isLoading by viewModel.isChatLoading.collectAsState()
    var messageText by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.fillMaxHeight(0.85f).padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "স্বর্ণালি স্মার্ট সহকারী (AI)",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { viewModel.clearChat() }) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = "Clear Chat", tint = Color.Gray)
                }
            }
            
            Box(modifier = Modifier.weight(1f).padding(vertical = 8.dp)) {
                val scrollState = rememberLazyListState()
                LaunchedEffect(chatMessages.size) {
                    if (chatMessages.isNotEmpty()) {
                        scrollState.animateScrollToItem(chatMessages.size - 1)
                    }
                }
                
                LazyColumn(state = scrollState, modifier = Modifier.fillMaxSize()) {
                    items(chatMessages) { msg ->
                        ChatBubble(msg)
                    }
                    if (isLoading) {
                        item {
                            Box(modifier = Modifier.padding(8.dp)) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                            }
                        }
                    }
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp, top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("জিজ্ঞাসা করুন...") },
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 3
                )
                FloatingActionButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            viewModel.sendChatMessage(messageText)
                            messageText = ""
                        }
                    },
                    containerColor = Color(0xFF765B40),
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
fun ChatBubble(msg: JewelryViewModel.ChatMessage) {
    val isModel = msg.role == "model"
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalAlignment = if (isModel) Alignment.Start else Alignment.End
    ) {
        Card(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isModel) 0.dp else 16.dp,
                bottomEnd = if (isModel) 16.dp else 0.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isModel) Color(0xFFF3EFEA) else Color(0xFF7D5800)
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = msg.content,
                modifier = Modifier.padding(12.dp),
                color = if (isModel) Color.Black else Color.White,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun VoiceInteractionOverlay(viewModel: JewelryViewModel, onDismiss: () -> Unit) {
    val result by viewModel.voiceCommandResult.collectAsState()
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1B16)),
            modifier = Modifier.size(280.dp).padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.Mic,
                    contentDescription = null,
                    tint = Color(0xFFFFDDB3),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = if (result.isEmpty()) "বলুন, আমি শুনছি..." else result,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                
                if (result.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    TextButton(onClick = onDismiss) {
                        Text("ঠিক আছে", color = Color(0xFFFFDDB3))
                    }
                }
            }
        }
    }
    
    // Simulate voice entry
    LaunchedEffect(Unit) {
        if (result.isEmpty()) {
            kotlinx.coroutines.delay(2000)
            viewModel.processVoiceCommand("আজকের স্টক কি?")
        }
    }
}

@Composable
fun CustomerLedgerDialog(
    viewModel: JewelryViewModel,
    customer: Customer,
    onDismiss: () -> Unit
) {
    val transactions by viewModel.getCustomerTransactions(customer.id).collectAsState(initial = emptyList())
    val totalPurchased = transactions.filter { it.transactionType == "Purchase" || it.transactionType == "বিক্রয়" }.sumOf { it.amountBdt }
    val totalPaid = transactions.sumOf { it.paidBdt }
    val totalDue = transactions.sumOf { it.dueBdt }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f).padding(8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = customer.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(text = customer.phone, fontSize = 12.sp, color = Color.Gray)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats Cards
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LedgerStatCard("মোট ক্রয়", "৳ ${String.format("%,.0f", totalPurchased)}", Color(0xFF7D5800), Modifier.weight(1f))
                    LedgerStatCard("পরিশোধ", "৳ ${String.format("%,.0f", totalPaid)}", Color(0xFF2E7D32), Modifier.weight(1f))
                    LedgerStatCard("মোট বকেয়া", "৳ ${String.format("%,.0f", totalDue)}", Color(0xFFC62828), Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(20.dp))
                Text("লেনদেনের খতিয়ান (Transaction History)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(transactions) { txn ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF9F6)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(txn.itemDescription, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    Text(
                                        java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault()).format(txn.date),
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                    Text(txn.transactionType, fontSize = 10.sp, color = Color(0xFF7D5800))
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("৳ ${String.format("%,.0f", txn.amountBdt)}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    if (txn.dueBdt > 0) {
                                        Text("বাকি: ৳ ${String.format("%,.0f", txn.dueBdt)}", color = Color(0xFFC62828), fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
                
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF765B40))
                ) {
                    Text("ফিরে যান", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun LedgerStatCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, fontSize = 9.sp, color = Color.Gray)
            Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun AddSupplierDialog(onDismiss: () -> Unit, onConfirm: (String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("নতুন মহাজন (Supplier) যোগ করুন", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("নাম") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = contact, onValueChange = { contact = it }, label = { Text("যোগাযোগ (ফোন)") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("ঠিকানা") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { Button(onClick = { if (name.isNotBlank()) onConfirm(name, contact, address) }) { Text("সংরক্ষণ করুন") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("বাতিল") } }
    )
}

@Composable
fun AddArtisanDialog(onDismiss: () -> Unit, onConfirm: (String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var specialty by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("নতুন কারিগর যোগ করুন", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("নাম") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = contact, onValueChange = { contact = it }, label = { Text("যোগাযোগ (ফোন)") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = specialty, onValueChange = { specialty = it }, label = { Text("বিশেষত্ব (Specialty)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { Button(onClick = { if (name.isNotBlank()) onConfirm(name, contact, specialty) }) { Text("সংরক্ষণ করুন") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("বাতিল") } }
    )
}

@Composable
fun AddEmployeeDialog(onDismiss: () -> Unit, onConfirm: (String, String, String, Double) -> Unit) {
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var salary by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("নতুন কর্মচারী যোগ করুন", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("নাম") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = role, onValueChange = { role = it }, label = { Text("পদবী (Role)") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("ফোন") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = salary, 
                    onValueChange = { salary = it }, 
                    label = { Text("বেতন (৳)") }, 
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), 
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = { Button(onClick = { if (name.isNotBlank()) onConfirm(name, role, phone, salary.toDoubleOrNull() ?: 0.0) }) { Text("সংরক্ষণ করুন") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("বাতিল") } }
    )
}

@Composable
fun AddBranchDialog(onDismiss: () -> Unit, onConfirm: (String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var loc by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("নতুন শাখা যোগ করুন", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("শাখার নাম") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = loc, onValueChange = { loc = it }, label = { Text("অবস্থান") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("ফোন") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { Button(onClick = { if (name.isNotBlank()) onConfirm(name, loc, phone) }) { Text("সংরক্ষণ করুন") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("বাতিল") } }
    )
}

@Composable
fun AddBankAccountDialog(onDismiss: () -> Unit, onConfirm: (String, String, Double) -> Unit) {
    var name by remember { mutableStateOf("") }
    var no by remember { mutableStateOf("") }
    var balance by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("নতুন ব্যাংক একাউন্ট যোগ করুন", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("ব্যাংকের নাম") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = no, onValueChange = { no = it }, label = { Text("একাউন্ট নম্বর") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = balance, 
                    onValueChange = { balance = it }, 
                    label = { Text("বর্তমান ব্যালেন্স (৳)") }, 
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), 
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = { Button(onClick = { if (name.isNotBlank()) onConfirm(name, no, balance.toDoubleOrNull() ?: 0.0) }) { Text("সংরক্ষণ করুন") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("বাতিল") } }
    )
}

@Composable
fun AddBusinessAccountDialog(onDismiss: () -> Unit, onConfirm: (String, String, Double, String) -> Unit) {
    var type by remember { mutableStateOf("Expense") }
    var category by remember { mutableStateOf("Rent") }
    var amount by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val categories = listOf("Salary", "Rent", "Utility", "Sale", "Investment", "Others")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("নতুন আয়-ব্যয় এন্ট্রি", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    FilterChip(selected = type == "Income", onClick = { type = "Income" }, label = { Text("আয়") })
                    FilterChip(selected = type == "Expense", onClick = { type = "Expense" }, label = { Text("ব্যয়") })
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("বিভাগ নির্বাচন করুন:", fontSize = 12.sp)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(categories) {
                        FilterChip(selected = category == it, onClick = { category = it }, label = { Text(it) })
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = amount, 
                    onValueChange = { amount = it }, 
                    label = { Text("পরিমাণ (৳)") }, 
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), 
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("মন্তব্য (ঐচ্ছিক)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { Button(onClick = { if (amount.isNotBlank()) onConfirm(type, category, amount.toDoubleOrNull() ?: 0.0, notes) }) { Text("সংরক্ষণ করুন") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("বাতিল") } }
    )
}
