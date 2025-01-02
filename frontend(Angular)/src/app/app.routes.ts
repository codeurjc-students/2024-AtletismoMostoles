import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { IndexComponent } from './components/index/index.component';
import { RankingComponent } from './components/ranking/ranking.component';
import {ClubMembersComponent} from './components/clubmembers/clubmembers.component';

export const routes: Routes = [
  { path: '', component: IndexComponent },
  { path: 'ranking', component: RankingComponent },
  { path: 'miembros', component: ClubMembersComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
