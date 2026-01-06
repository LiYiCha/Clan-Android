package com.yc.document.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yc.document.ui.screen.DocumentDetailScreen
import com.yc.document.ui.screen.DocumentListScreen
import com.yc.document.ui.viewmodel.DocumentViewModel
import com.yc.ui.theme.CMSTheme

/**
 * 文档管理 Activity
 */
class DocumentActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            CMSTheme {
                val viewModel: DocumentViewModel = viewModel()
                
                var currentScreen by remember { mutableStateOf(DocumentScreenType.LIST) }
                var selectedDocId by remember { mutableIntStateOf(0) }
                
                when (currentScreen) {
                    DocumentScreenType.LIST -> {
                        DocumentListScreen(
                            viewModel = viewModel,
                            onBackClick = { finish() },
                            onDocumentClick = { docId ->
                                selectedDocId = docId
                                currentScreen = DocumentScreenType.DETAIL
                            }
                        )
                    }
                    DocumentScreenType.DETAIL -> {
                        DocumentDetailScreen(
                            docId = selectedDocId,
                            viewModel = viewModel,
                            onBackClick = {
                                viewModel.clearDetailState()
                                currentScreen = DocumentScreenType.LIST
                            }
                        )
                    }
                }
            }
        }
    }
}

private enum class DocumentScreenType {
    LIST,
    DETAIL
}
