import { Component, OnInit, Input } from '@angular/core';
import { Property } from 'src/app/model/Property.model';
import { Router } from '@angular/router';
import { RealEstateService } from 'src/app/services/RealEstate.service';

@Component({
  selector: 'app-realestate-listing',
  templateUrl: './realestate-listing.component.html',
  styleUrls: ['./realestate-listing.component.css']
})
export class RealestateListingComponent implements OnInit {
  @Input() type: string;
  dataSource: Property[];
  filterLocation: string;
  filterType: string;
  filterPrice: number;
  filterSize: number;
  filteredProperties: Property[];
  searchParams: any;
  constructor(
    private router: Router,
    private realEstateService: RealEstateService) { }

  ngOnInit(): void {
    if (this.type == 'views') {
      //this.getPropertiesByViews();
    } else if (this.type == 'location'){
      //this.getPropertiesByLocation();
    }
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

}
sortByPrice(){
  
}
sortByLand(){
  
}
sortBySize(){
  
}
addSearchParams(event, site: number){
  //Change search params
}
}
