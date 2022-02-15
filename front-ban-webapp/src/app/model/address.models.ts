export class Address {
  internalId: string;
  id: string;
  idFantoir: string;
  numero: string;
  nomVoie: string;
  zipCode: string;
  inseeCode: string;
  cityName: string;
  longitude: number;
  latitude: number;
  labelForwarding: string;

  constructor(obj?: Partial<Address>) {
    Object.assign(this, obj);
  }
}