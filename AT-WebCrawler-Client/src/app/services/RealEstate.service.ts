import { Injectable } from '@angular/core';
import { HttpHeaders, HttpClient } from '@angular/common/http';
import { map, catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { environment } from 'src/environments/environment';
const API_URL = environment.API_URL;
@Injectable({
  providedIn: 'root'
})
export class RealEstateService {


constructor(private http: HttpClient) { }
getPropertiesByViews(searchParams){
    let headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    return this.http.get(`/brand`, { headers, observe: 'response' })
      .pipe(
        map(response => {
          return response.body;
        }),
        catchError((response) => {
          return throwError(response.error);
        })
      );
  
}
getPropertiesByLocation(){
  let headers = new HttpHeaders({
    'Content-Type': 'application/json'
  });
  return this.http.get(`/brand`, { headers, observe: 'response' })
    .pipe(
      map(response => {
        return response.body;
      }),
      catchError((response) => {
        return throwError(response.error);
      })
    );

}

startStatistics(message){
  const headers = new HttpHeaders({
    'Content-Type': 'application/json',
  });

  return this.http.post(`${API_URL}/properties`, message, { headers, observe: 'response' })
    .pipe(
      map(response => {
        return response.body;
      }),
      catchError((response) => {
        return throwError(response.error);
      })
    );
}
}
