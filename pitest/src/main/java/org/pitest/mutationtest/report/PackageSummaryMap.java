package org.pitest.mutationtest.report;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class PackageSummaryMap {

  private final Map<String, PackageSummaryData> packageSummaryData = new TreeMap<String, PackageSummaryData>();

  private PackageSummaryData getPackageSummaryData(final String packageName) {
    PackageSummaryData psData;
    if (this.packageSummaryData.containsKey(packageName)) {
      psData = this.packageSummaryData.get(packageName);
    } else {
      psData = new PackageSummaryData(packageName);
      this.packageSummaryData.put(packageName, psData);
    }
    return psData;
  }

  public void add(final String packageName, final MutationTestSummaryData data) {
    getPackageSummaryData(packageName).addSummaryData(data);
  }

  public Collection<PackageSummaryData> values() {
    return this.packageSummaryData.values();
  }

}