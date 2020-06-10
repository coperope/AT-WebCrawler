import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { CookieService } from 'ngx-cookie-service';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { MainPageComponent } from './components/main-page/main-page.component';
import { AgentTypesListingComponent } from './components/agent-types-listing/agent-types-listing.component';
import { RunningAgentsListingComponent } from './components/running-agents-listing/running-agents-listing.component';

@NgModule({
  declarations: [
    AppComponent,
    MainPageComponent,
    AgentTypesListingComponent,
    RunningAgentsListingComponent,
  ],
  imports: [
    BrowserModule,
    FormsModule,
    AppRoutingModule,
    NgbModule,
    HttpClientModule,
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
