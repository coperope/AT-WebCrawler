import { Component, OnInit, Input } from '@angular/core';
import { Property } from 'src/app/model/Property.model';

@Component({
  selector: 'app-estate-teaser',
  templateUrl: './estate-teaser.component.html',
  styleUrls: ['./estate-teaser.component.css']
})
export class EstateTeaserComponent implements OnInit {
@Input() estate: Property;
  constructor() { }

  ngOnInit() {
  }

}
