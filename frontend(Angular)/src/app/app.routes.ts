import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { IndexComponent } from './components/index/index.component';
import { RankingComponent } from './components/ranking/ranking.component';

export const routes: Routes = [
  { path: '', component: IndexComponent },
  { path: 'ranking', component: RankingComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
