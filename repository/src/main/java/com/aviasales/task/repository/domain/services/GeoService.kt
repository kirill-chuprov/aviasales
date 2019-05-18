package com.aviasales.task.repository.domain.services

import com.google.android.gms.maps.model.LatLng
import io.reactivex.Observable

interface GeoService {
  fun computeBezierPoints(from: LatLng, to: LatLng): Observable<List<LatLng>>
}