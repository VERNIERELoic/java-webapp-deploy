import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { JobResult } from 'src/app/model/job-result.model';

export interface DialogData {
  results: JobResult[];
}

@Component({
  selector: 'app-result-import-modal',
  templateUrl: './result-import-modal.component.html',
  styleUrls: [ './result-import-modal.component.scss' ]
})
export class ResultImportModalComponent {
  results: JobResult[];
  selected: JobResult;
  displayedColumns: string[] = [ 'exitStatus', 'file', 'details', 'duration', 'writeCount', 'skipReadCount', 'errors' ];

  constructor(public dialogRef: MatDialogRef<ResultImportModalComponent>,
              @Inject(MAT_DIALOG_DATA) public data: DialogData) {
    this.results = data.results;
  }
}
