import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { MainPageComponent } from './components/main-page/main-page.component';
import { RealestateComponent } from './components/realestate/realestate.component';


const routes: Routes = [
  {
    path: '',
    component: MainPageComponent,
    canActivate: [],
    children: []
  },
  {
    path: 'agent-administration',
    component: MainPageComponent,
    canActivate: [],
    children: []
  },
  {
    path: 'realestate',
    component: RealestateComponent,
    canActivate: [],
    children: []
  },

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
