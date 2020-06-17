import { Component, OnInit, Input } from '@angular/core';
import { Property } from 'src/app/model/Property.model';
import { Router } from '@angular/router';
import { RealEstateService } from 'src/app/services/RealEstate.service';
import { webSocket } from 'rxjs/webSocket';
import { environment } from '../../../environments/environment';
import { WSMessage } from 'src/app/model/WSMessage.model';
import { DataRequestDTO } from 'src/app/model/DataRequestDTO.model';

@Component({
  selector: 'app-realestate-listing',
  templateUrl: './realestate-listing.component.html',
  styleUrls: ['./realestate-listing.component.css']
})
export class RealestateListingComponent implements OnInit {
  // websocket = webSocket(this.getWebsocketUrl());
  
  @Input() type: string;
  @Input() dataSource: Property[];
  filterLocation: string;
  filterType: string;
  filterPrice: number;
  filterSize: number;
  @Input() filteredProperties: Property[];
  ontology: string;
  checkedBoxes: boolean[] = [true, true, true];
  searchParams: DataRequestDTO = new DataRequestDTO();
  constructor(
    private router: Router,
    private realEstateService: RealEstateService) {
      // this.websocket.asObservable().subscribe(
      //   data => {
      //     const message = data as WSMessage;
      //     if (message.type === 'Top100') {
      //       this.dataSource = message.top100;
      //       this.filteredProperties = this.dataSource;
      //       console.log(this.dataSource);
      //     }
      //   },
      //   err => console.log(err),
      //   () => console.log('WS connection closed')
      // );
     }

  ngOnInit(): void {
    if (this.type == 'views') {
      this.searchParams.content = ["properties/city-nekretnine.json","properties/info-nekretnine.json", "properties/021-nekretnine.json"];
      this.searchParams.ontology = "TOP-100:SORT-VIEWS-DESC";
      this.ontology = "TOP-100";
    } else if (this.type == 'location'){
      this.searchParams.content = ["properties/city-nekretnine.json","properties/info-nekretnine.json", "properties/021-nekretnine.json"];
      this.searchParams.ontology = "TOP-100-LOCATION:SORT-VIEWS-DESC";
      this.ontology = "TOP-100-LOCATION";
    }
    this.realEstateService.startStatistics(this.searchParams).subscribe(
      (data: any) => {
        console.log(data);
      },
      (error) => {
        alert(error);
      }
    );
  }
getPropertiesByViews(searchParams){
  this.realEstateService.getPropertiesByViews(searchParams).subscribe(
    (data: any) => {
      this.dataSource = data;
      this.filteredProperties = this.dataSource;
      console.log(this.dataSource);
    },
    (error) => {
      alert(error);
    });
}

getPropertiesByLocation(){
  this.realEstateService.getPropertiesByLocation().subscribe(
    (data: any) => {
      this.dataSource = data;
      console.log(this.dataSource);
      this.filteredProperties = this.dataSource;
    },
    (error) => {
      alert(error);
    });
}

onFilterByLocation(){
  this.filterType = '';
  this.filterPrice = undefined;
  this.filterSize = undefined;
  this.filteredProperties = this.dataSource.filter(estate =>
    estate.location.toLocaleLowerCase().includes(this.filterLocation.toLocaleLowerCase()));
}
onFilterByType(){
  this.filterLocation = '';
  this.filterPrice = undefined;
  this.filterSize = undefined;
  this.filteredProperties = this.dataSource.filter(estate =>
    estate.type.toLocaleLowerCase().includes(this.filterType.toLocaleLowerCase()));
}
onFilterByPrice(){
  this.filterLocation = '';
  this.filterType = '';
  this.filterSize = undefined;
  this.filteredProperties = this.dataSource.filter(estate =>
    estate.price <= this.filterPrice);
}
onFilterBySize(){
  this.filterLocation = '';
  this.filterType = '';
  this.filterPrice = undefined;
  this.filteredProperties = this.dataSource.filter(estate =>
    estate.size <= this.filterSize);
}
// Send request with adjusted params (DTO)
sortByViews(){
 

  this.searchParams.ontology = this.ontology + ":SORT-VIEWS-DESC";
  this.realEstateService.startStatistics(this.searchParams).subscribe(
    (data: any) => {
      console.log(data);
    },
    (error) => {
      alert(error);
    }
  );
}
sortByPrice(){
    this.searchParams.ontology = this.ontology + ":SORT-PRICE-DESC";
  this.realEstateService.startStatistics(this.searchParams).subscribe(
    (data: any) => {
      console.log(data);
    },
    (error) => {
      alert(error);
    }
  );
}
sortByLand(){
  this.searchParams.ontology = this.ontology + ":SORT-LAND-DESC";
  this.realEstateService.startStatistics(this.searchParams).subscribe(
    (data: any) => {
      console.log(data);
    },
    (error) => {
      alert(error);
    }
  );
}
sortBySize(){
  this.searchParams.ontology = this.ontology + ":SORT-SIZE-DESC";
  this.realEstateService.startStatistics(this.searchParams).subscribe(
    (data: any) => {
      console.log(data);
    },
    (error) => {
      alert(error);
    }
  );
}
searchLocation(){
  this.searchParams.ontology = this.ontology + ":SORT-SIZE-DESC";
  this.realEstateService.startStatistics(this.searchParams).subscribe(
    (data: any) => {
      console.log(data);
    },
    (error) => {
      alert(error);
    }
  );
}
addSearchParams(event, site: number){
  let estates = ["properties/city-nekretnine.json",  "properties/info-nekretnine.json", "properties/021-nekretnine.json"];
  this.searchParams.content = [];
  this.checkedBoxes[site] = !this.checkedBoxes[site];
  for(let i= 0; i < 3; i++ ){
    if (this.checkedBoxes[i]){
      this.searchParams.content.push(estates[i]);
    }
  }
}

getWebsocketUrl() {
  var loc = window.location, new_uri;
  if (loc.protocol === 'https:') {
    new_uri = 'wss:';
  } else {
    new_uri = 'ws:';
  }
  new_uri += '//' + loc.host;
  new_uri += loc.pathname + 'ws';
  //console.log(new_uri);
  return environment.production ? new_uri : environment.WS_URL;
}
}
