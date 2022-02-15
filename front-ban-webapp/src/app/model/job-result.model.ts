export class JobResult {
  file: string;
  details: string;
  duration: string;
  writeCount: number;
  skipReadCount: number;
  errors: string[];
  exitStatus: string;

  constructor(obj?: Partial<JobResult>) {
    Object.assign(this, obj);
  }
}