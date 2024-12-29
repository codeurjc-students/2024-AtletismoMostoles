import { NgModule } from '@angular/core';
import {CommonModule, NgOptimizedImage} from '@angular/common';
import { IndexComponent } from './index.component';
import {RouterLink} from '@angular/router';

@NgModule({
  declarations: [IndexComponent],
  imports: [CommonModule, NgOptimizedImage, RouterLink],
  exports: [IndexComponent]
})
export class IndexModule {}
