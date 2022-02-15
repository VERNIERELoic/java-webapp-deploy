import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'statusIcon'
})
export class StatusIconPipe implements PipeTransform {

  transform(exitStatus: string): unknown {
    return exitStatus === 'COMPLETED' ? 'done' : 'highlight_off';
  }
}
