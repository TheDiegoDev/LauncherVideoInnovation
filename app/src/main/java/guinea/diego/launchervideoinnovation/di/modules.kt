package guinea.diego.launchervideoinnovation.di


import guinea.diego.launchervideoinnovation.ui.home.BrowserFragmentViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewmodelModule = module {
    viewModel { BrowserFragmentViewModel() }
}