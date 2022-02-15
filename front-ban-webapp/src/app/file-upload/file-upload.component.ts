import { HttpClient } from '@angular/common/http';
import { Component, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { NotifierService } from 'angular-notifier';
import { Subscription } from 'rxjs';
import { JobResult } from 'src/app/model/job-result.model';
import { ResultImportModalComponent } from 'src/app/result-import-modal/result-import-modal.component';
import { CustomHttpClientService } from 'src/app/services/custom-http-client.service';

@Component({
  selector: 'app-file-upload',
  templateUrl: './file-upload.component.html'
})
export class FileUploadComponent implements OnDestroy {

  uploadForm: FormGroup;
  uploading = false;
  response$: Subscription | undefined;

  constructor(private http: CustomHttpClientService, private formBuilder: FormBuilder, private notifier: NotifierService,
              private dialog: MatDialog) {
    this.uploadForm = this.formBuilder.group({
      files: [ null, [ Validators.required ] ]
    });
  }

  ngOnDestroy(): void {
    this.response$?.unsubscribe();
  }

  onFileSelected(event: any) {
    this.submit(event.target.files);
  }

  submit(fileList: FileList | null) {
    if (fileList) {
      const formData = new FormData();
      for (let i = 0; i < fileList.length; i++) {
        formData.append('file', fileList[i], fileList[i].name);
      }
      this.uploading = true;
      this.response$ = this.http.post<JobResult>('/address', formData).subscribe(jobResult => {
        this.uploading = false;
        return this.dialog.open(ResultImportModalComponent, {
          width: '800px',
          minHeight: '105px',
          data: {
            results: jobResult
          }
        }).afterOpened().subscribe();
      }, error => {
        this.uploading = false;
        this.notifier.notify('warning', error);
      });
    }
  }
}
