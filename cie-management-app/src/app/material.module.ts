import {
  MatButtonModule, MatCardModule,
  MatCheckboxModule, MatDatepickerModule, MatDialogModule, MatExpansionModule,
  MatIconModule, MatInputModule, MatListModule,
  MatMenuModule, MatProgressSpinnerModule, MatSelectModule,
  MatSidenavModule, MatSlideToggleModule, MatSnackBarModule, MatTableModule,
  MatToolbarModule
} from '@angular/material';
import {NgModule} from '@angular/core';

// All available material modules.
// Go ahead and add your own in case you need it.
const MATERIAL_MODULES = [
  MatButtonModule,
  MatCheckboxModule,
  MatToolbarModule,
  MatSidenavModule,
  MatMenuModule,
  MatIconModule,
  MatCardModule,
  MatInputModule,
  MatTableModule,
  MatSnackBarModule,
  MatProgressSpinnerModule,
  MatDialogModule,
  MatSelectModule,
  MatListModule,
  MatDatepickerModule,
  MatSlideToggleModule,
  MatExpansionModule
];

@NgModule({
  imports: MATERIAL_MODULES,
  exports: MATERIAL_MODULES
})
export class CustomMaterialModule {
}
