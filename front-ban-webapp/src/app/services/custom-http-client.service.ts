import { HttpClient } from '@angular/common/http';
import { Injectable, OnDestroy } from '@angular/core';
import { Observable, Subscription } from 'rxjs';

interface Config {
  url: string;
}

@Injectable({
  providedIn: 'root'
})
export class CustomHttpClientService implements OnDestroy{
  sub: Subscription;

  baseUrl: string;
  constructor(private http: HttpClient) {
    this.sub = this.http.get<Config>('assets/config.json').subscribe(config => {
      this.baseUrl = config.url;
    });
  }


  get<T>(url: string): Observable<T> {
    return this.http.get<T>(`${this.baseUrl}${url}`);
  }

  post<T>(url: string, body: any): Observable<T> {
    return this.http.post<T>(`${this.baseUrl}${url}`, body);
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe()
  }

}
