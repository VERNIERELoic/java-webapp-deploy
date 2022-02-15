import { HttpClient } from '@angular/common/http';
import { Component, OnDestroy } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { Observable, of, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged, filter, tap } from 'rxjs/operators';
import { Address } from 'src/app/model/address.models';
import { CustomHttpClientService } from 'src/app/services/custom-http-client.service';

@Component({
  selector: 'app-search-form',
  templateUrl: './search-form.component.html'
})
export class SearchFormComponent implements OnDestroy {

  searchTerms = new FormControl(null, Validators.required);
  result$: Observable<Address[]> = of();
  private sub: Subscription;
  writing = false;
  displayedColumns: string[] = [ 'displayName', 'longitude', 'latitude', 'action' ];

  constructor(private http: CustomHttpClientService) {
    this.sub = this.searchTerms.valueChanges.pipe(
      tap(() => this.writing = true),
      tap(() => this.result$ = of()),
      debounceTime(300),
      distinctUntilChanged(),
      filter(value => !!value),
      tap(terms => this.result$ = this.http.get<Address[]>(`/address?terms=${terms}`).pipe(
        tap(() => this.writing = false)
      ))
    ).subscribe();
  }

  displayName(address: Address) {
    const original = `${address.numero} ${address.nomVoie} (${address.zipCode}: ${address.cityName})`;
    const indexOf = original.toLowerCase().indexOf(this.searchTerms.value.toLowerCase());
    // Ca met en gras la recherche, pas très élegant :(
    return `${original.slice(0, indexOf)}${original.slice(indexOf, indexOf + this.searchTerms.value.length).bold()}${original.slice(indexOf + this.searchTerms.value.length, original.length)}`;
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  displayGoogle(element: Address) {
    return `https://www.google.com/maps/search/?api=1&query=${element.latitude},${element.longitude}`;

  }
}
