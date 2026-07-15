package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ui.theme.UserTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class TerminalOutput(
    val value: String,
    val isError: Boolean = false
)

private data class CommandResult(
    val output: String = "",
    val isError: Boolean = false,
    val clearScreen: Boolean = false,
    val closeSession: Boolean = false
)

@Composable
fun TerminalTabContent(
    viewModel: JewelryViewModel,
    customerCount: Int,
    inventoryCount: Int,
    transactionCount: Int,
    modifier: Modifier = Modifier
) {
    val appTheme by viewModel.appTheme.collectAsState()
    val isOffline by viewModel.isOffline.collectAsState()

    val commands = remember {
        listOf(
            "help", "clear", "exit", "status", "echo", "date",
            "customers", "inventory", "transactions", "theme",
            "offline", "history", "cat", "grep", "wc", "constraints",
            "copilot", "longtask", "interrupt"
        )
    }
    var commandInput by rememberSaveable { mutableStateOf("") }
    var sessionOpen by rememberSaveable { mutableStateOf(true) }
    var history by rememberSaveable { mutableStateOf(listOf<String>()) }
    var outputs by rememberSaveable { mutableStateOf(listOf<TerminalOutput>()) }
    val redirects = remember { mutableStateMapOf<String, String>() }
    val listState = rememberLazyListState()
    val keyboard = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    var activeTask by remember { mutableStateOf<Job?>(null) }

    DisposableEffect(Unit) {
        onDispose { activeTask?.cancel() }
    }

    LaunchedEffect(outputs.size) {
        if (outputs.isNotEmpty()) {
            listState.animateScrollToItem(outputs.lastIndex)
        }
    }

    fun appendOutput(text: String, isError: Boolean = false) {
        if (text.isBlank()) return
        outputs = outputs + TerminalOutput(text, isError)
    }

    fun runSingleCommand(rawCommand: String, pipedInput: String? = null): CommandResult {
        val trimmed = rawCommand.trim()
        if (trimmed.isBlank()) return CommandResult()

        val redirectParts = trimmed.split(">", limit = 2)
        val commandPart = redirectParts[0].trim()
        val redirectTarget = redirectParts.getOrNull(1)?.trim()
        if (redirectTarget != null && redirectTarget.contains("/")) {
            return CommandResult(
                output = "Redirection to filesystem paths is blocked. Use a simple in-memory name.",
                isError = true
            )
        }

        val parts = commandPart.split(" ").filter { it.isNotBlank() }
        if (parts.isEmpty()) return CommandResult()

        val name = parts.first().lowercase(Locale.ROOT)
        val args = parts.drop(1)

        val baseResult = when (name) {
            "help" -> CommandResult(
                output = """
                    Available commands:
                    help, clear, exit, status, history, date, echo <text>
                    customers, inventory, transactions
                    theme <light|black|blue|green|red|purple>
                    offline <on|off>, cat <name>, grep <text>, wc
                    constraints, copilot, longtask, interrupt
                    Pipes: command1 | grep word
                    Redirect: command > noteName
                """.trimIndent()
            )
            "clear" -> CommandResult(clearScreen = true)
            "exit" -> CommandResult(output = "Terminal session closed. Type any command to reopen.", closeSession = true)
            "status" -> CommandResult(
                output = "Theme=$appTheme | Offline=$isOffline | Customers=$customerCount | Inventory=$inventoryCount | Transactions=$transactionCount"
            )
            "date" -> CommandResult(
                output = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            )
            "echo" -> CommandResult(output = if (args.isEmpty()) pipedInput.orEmpty() else args.joinToString(" "))
            "customers" -> CommandResult(output = "Total customers: $customerCount")
            "inventory" -> CommandResult(output = "Total inventory items: $inventoryCount")
            "transactions" -> CommandResult(output = "Total transactions: $transactionCount")
            "theme" -> {
                val selected = args.firstOrNull()?.lowercase(Locale.ROOT)
                val target = when (selected) {
                    "light" -> UserTheme.LIGHT
                    "black" -> UserTheme.BLACK
                    "blue" -> UserTheme.BLUE
                    "green" -> UserTheme.GREEN
                    "red" -> UserTheme.RED
                    "purple" -> UserTheme.PURPLE
                    else -> null
                }
                if (target == null) {
                    CommandResult(output = "Usage: theme <light|black|blue|green|red|purple>", isError = true)
                } else {
                    viewModel.setTheme(target)
                    CommandResult(output = "Theme changed to $target")
                }
            }
            "offline" -> {
                when (args.firstOrNull()?.lowercase(Locale.ROOT)) {
                    "on" -> {
                        viewModel.setOfflineMode(true)
                        CommandResult(output = "Offline mode enabled.")
                    }
                    "off" -> {
                        viewModel.setOfflineMode(false)
                        CommandResult(output = "Offline mode disabled.")
                    }
                    else -> CommandResult(output = "Usage: offline <on|off>", isError = true)
                }
            }
            "history" -> {
                if (history.isEmpty()) CommandResult(output = "No command history yet.")
                else CommandResult(output = history.takeLast(20).joinToString("\n") { "• $it" })
            }
            "cat" -> {
                val key = args.firstOrNull()
                when {
                    key.isNullOrBlank() -> CommandResult(output = "Usage: cat <name>", isError = true)
                    redirects[key] == null -> CommandResult(output = "No redirected output found for '$key'.", isError = true)
                    else -> CommandResult(output = redirects[key].orEmpty())
                }
            }
            "grep" -> {
                val needle = args.joinToString(" ").trim()
                val source = pipedInput.orEmpty()
                if (needle.isBlank()) {
                    CommandResult(output = "Usage with pipe: command | grep <text>", isError = true)
                } else {
                    val filtered = source.lines().filter { it.contains(needle, ignoreCase = true) }
                    if (filtered.isEmpty()) CommandResult(output = "No matching lines.")
                    else CommandResult(output = filtered.joinToString("\n"))
                }
            }
            "wc" -> {
                val source = pipedInput.orEmpty()
                val lines = if (source.isBlank()) 0 else source.lines().size
                val words = source.split(Regex("\\s+")).filter { it.isNotBlank() }.size
                val chars = source.length
                CommandResult(output = "lines=$lines words=$words chars=$chars")
            }
            "constraints" -> CommandResult(
                output = """
                    Android constraints:
                    - This terminal is custom and sandboxed (not PowerShell/CMD).
                    - No unrestricted shell/process execution is exposed.
                    - Background work follows Android app lifecycle limits.
                    - File redirection is in-memory only for safety.
                """.trimIndent()
            )
            "copilot" -> CommandResult(
                output = "GitHub Copilot CLI desktop install docs do not directly apply to Android runtime. Use API-backed assistant features in-app."
            )
            "longtask" -> {
                if (activeTask?.isActive == true) {
                    CommandResult(output = "A task is already running. Use 'interrupt' to cancel.", isError = true)
                } else {
                    activeTask = scope.launch {
                        delay(5000)
                        appendOutput("[task] Completed safely.")
                    }
                    CommandResult(output = "Long task started. Type 'interrupt' to cancel.")
                }
            }
            "interrupt" -> {
                if (activeTask?.isActive == true) {
                    activeTask?.cancel()
                    activeTask = null
                    CommandResult(output = "Active task interrupted safely.")
                } else {
                    CommandResult(output = "No active task to interrupt.")
                }
            }
            else -> CommandResult(output = "Unknown command: $name. Type 'help'.", isError = true)
        }

        if (!redirectTarget.isNullOrBlank() && baseResult.output.isNotBlank()) {
            redirects[redirectTarget] = baseResult.output
            return CommandResult(output = "Saved output to '$redirectTarget'.")
        }
        return baseResult
    }

    fun executeCommand(rawInput: String) {
        val cleaned = rawInput.trim()
        if (cleaned.isBlank()) return

        if (!sessionOpen) {
            sessionOpen = true
            appendOutput("Terminal session reopened.")
        }

        history = history + cleaned
        appendOutput("\$ $cleaned")

        val pipeSegments = cleaned.split("|").map { it.trim() }.filter { it.isNotBlank() }
        var pipeData: String? = null
        var finalResult = CommandResult()

        pipeSegments.forEachIndexed { index, segment ->
            val result = runSingleCommand(segment, pipeData)
            if (result.clearScreen) {
                outputs = emptyList()
            }
            if (result.closeSession) {
                sessionOpen = false
            }
            if (result.isError) {
                appendOutput(result.output, isError = true)
                finalResult = result
                return
            }
            pipeData = result.output
            finalResult = result
            if (index == pipeSegments.lastIndex && result.output.isNotBlank()) {
                appendOutput(result.output)
            }
        }

        if (finalResult.clearScreen) {
            appendOutput("Screen cleared.")
        }
    }

    val autocomplete = remember(commandInput, commands) {
        val key = commandInput.trim().lowercase(Locale.ROOT)
        if (key.isBlank()) emptyList() else commands.filter { it.startsWith(key) }.take(6)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF5EA)),
            shape = RoundedCornerShape(14.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Memory, contentDescription = null, tint = Color(0xFF7D5800))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Secure App CLI (Custom)",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF291800)
                    )
                    Text(
                        text = "Sandboxed terminal for Android app commands, not system CMD/PowerShell.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF504539)
                    )
                }
            }
        }

        Surface(
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF111315))
                    .padding(12.dp)
            ) {
                if (outputs.isEmpty()) {
                    Text(
                        text = "Type 'help' to start.\nUse pipes (|) and in-memory redirection (> name).",
                        color = Color(0xFF9FE89E),
                        fontFamily = FontFamily.Monospace
                    )
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        itemsIndexed(outputs) { _, item ->
                            Text(
                                text = item.value,
                                color = if (item.isError) Color(0xFFFF8A80) else Color(0xFFB8F5B1),
                                fontFamily = FontFamily.Monospace,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }

        if (autocomplete.isNotEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                autocomplete.forEach { suggestion ->
                    AssistChip(
                        onClick = { commandInput = suggestion },
                        label = { Text(suggestion, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = commandInput,
                onValueChange = { next ->
                    commandInput = if (next.length <= 180) next else next.take(180)
                },
                modifier = Modifier.weight(1f),
                singleLine = true,
                prefix = { Text("$") },
                placeholder = { Text("Enter command") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        val toRun = commandInput
                        commandInput = ""
                        executeCommand(toRun)
                        keyboard?.hide()
                    }
                ),
                enabled = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            TextButton(
                onClick = {
                    val toRun = commandInput
                    commandInput = ""
                    executeCommand(toRun)
                    keyboard?.hide()
                },
                enabled = commandInput.isNotBlank()
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Run")
            }
            TextButton(
                onClick = {
                    val previous = history.lastOrNull() ?: return@TextButton
                    commandInput = previous
                },
                enabled = history.isNotEmpty()
            ) {
                Icon(Icons.Default.ArrowUpward, contentDescription = "Use previous command")
            }
        }

        if (!sessionOpen) {
            Text(
                text = "Session is closed with 'exit'. Enter any command to reopen.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFB3261E)
            )
        }
    }
}
