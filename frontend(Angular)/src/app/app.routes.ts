import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { IndexComponent } from './components/index/index.component';
import { RankingComponent } from './components/ranking/ranking.component';
import { ClubMembersComponent } from './components/clubmembers/clubmembers.component';
import { EventsComponent } from './components/events/events.component';
import { EventsCalendarComponent } from './components/events-calendar/events-calendar.component';
import { EventDetailsComponent } from './components/event-details/event-details.component';
import { ProfileComponent } from './components/profile/profile.component';
import { AssociationAtlComponent } from './components/association-atl/association-atl.component';
import { DisciplineDetailsComponent } from './components/discipline-details/discipline-details.component';
import { NewEventFormComponent } from './components/new-event-form/new-event-form.component';
import { AuthGuard } from './guards/auth.guard';
import { LoginComponent } from './components/login/login.component';

export const routes: Routes = [
  { path: '', component: IndexComponent },
  { path: 'ranking', component: RankingComponent },
  { path: 'miembros', component: ClubMembersComponent },
  { path: 'eventos', component: EventsComponent },
  { path: 'calendario', component: EventsCalendarComponent },
  { path: 'eventos/:id', component: EventDetailsComponent },
  { path: 'profile/:type/:id', component: ProfileComponent,},
  { path: 'disciplines', component: AssociationAtlComponent },
  { path: 'discipline-details/:id', component: DisciplineDetailsComponent },
  {
    path: 'events/new',
    component: NewEventFormComponent,
    canActivate: [AuthGuard],
    data: { roles: ['USER', 'ADMIN'] },
  },
  { path: 'login', component: LoginComponent },
  { path: '**', redirectTo: '' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
