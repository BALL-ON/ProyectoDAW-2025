import { Injectable } from '@angular/core';
import { MensajeDTO } from '../interfaces/mensaje-dto';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class MensajeContacto {

  constructor(private http: HttpClient) { }

}
