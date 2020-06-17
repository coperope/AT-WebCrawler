import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { CookieService } from 'ngx-cookie-service';
import { NgSelectModule } from '@ng-select/ng-select';
import { MatTableModule } from '@angular/material/table';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { MainPageComponent } from './components/main-page/main-page.component';
import { AgentTypesListingComponent } from './components/agent-types-listing/agent-types-listing.component';
import { RunningAgentsListingComponent } from './components/running-agents-listing/running-agents-listing.component';
import { HeaderComponent } from './components/header/header.component';
import { RealestateComponent } from './components/realestate/realestate.component';
import { MatTabsModule } from '@angular/material/tabs';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations'; 
import { EstateTeaserComponent } from './components/estate-teaser/estate-teaser.component';
import { RealestateListingComponent } from './components/realestate-listing/realestate-listing.component';
import {MatCheckboxModule} from '@angular/material/checkbox';
import { StatisticsComponent } from './components/statistics/statistics.component';
import { ChartsModule } from 'ng2-charts';

@NgModule({
  declarations: [
    AppComponent,
    MainPageComponent,
    AgentTypesListingComponent,
    RunningAgentsListingComponent,
    HeaderComponent,
    RealestateComponent,
    AgentTypesListingComponent,
    EstateTeaserComponent,
    RealestateListingComponent,
    StatisticsComponent,
  ],
  imports: [
    MatTableModule,
    BrowserModule,
    FormsModule,
    AppRoutingModule,
    NgbModule,
    HttpClientModule,
    NgSelectModule,
    MatTabsModule,
    MatIconModule,
    MatTooltipModule,
    BrowserAnimationsModule,
    MatCheckboxModule,
    ChartsModule
  ],
  providers: [
     CookieService
  ],
  schemas: [
     CUSTOM_ELEMENTS_SCHEMA
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
