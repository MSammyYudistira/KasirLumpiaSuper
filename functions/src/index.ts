import * as functions from 'firebase-functions';
import axios from 'axios';

/**
* Create Midtrans Basic Auth Header using server_key from Firebase Functions config.
*/
function authHeader(): string {
  const key = process.env.MIDTRANS_SERVER_KEY || '';
  return 'Basic ' + Buffer.from(key + ':').toString('base64');
}

/* ========================================================================
   QRIS DIRECT API — CREATE QR CODE
   ======================================================================== */

/**
 * Callable function to generate a QRIS (Direct API)
 * Expected input:
 *  {
 *     orderId: "string",
 *     amount: number
 *  }
 */
export const createQris = functions.https.onCall(async (data, _context) => {
  try {
    console.log('===== [createQris] CALLED =====');
    console.log('RAW INPUT:', data);
    console.log('data.data:', data.data);

    const body = data.data || {};
    const orderId = body.orderId;
    const amount = body.amount;

    console.log('Parsed orderId:', orderId, 'amount:', amount);

    if (!orderId || !amount) {
      console.error('Missing orderId or amount');
      throw new Error('Missing orderId or amount');
    }

    const serverKey = process.env.MIDTRANS_SERVER_KEY;
    if (!serverKey) {
      console.error('Server key missing');
      throw new Error('Midtrans server key is not set');
    }

    console.log('Server key exists? ', !!serverKey);
    console.log('Auth header:', authHeader());

    const url = 'https://api.sandbox.midtrans.com/v2/charge';
    const payload = {
      payment_type: 'qris',
      transaction_details: {
        order_id: orderId,
        gross_amount: amount,
      },
    };

    console.log('[createQris] Calling Midtrans…');
    console.log('Payload:', payload);

    const response = await axios.post(url, payload, {
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'Authorization': authHeader(),
      },
    });

    console.log('[createQris] Midtrans Response:', response.data);

    const r = response.data;

    const qrUrl =
      r?.qr_url ||
      r?.actions?.[0]?.url ||
      null;

    console.log('[createQris] Extracted QR URL:', qrUrl);

    return {
      orderId,
      qrUrl,
      status: r?.transaction_status || 'pending',
    };
  } catch (err: any) {
    const msg = err?.response?.data || err?.message || 'Unknown error';

    console.error('[createQris] ERROR:', msg);

    throw new functions.https.HttpsError(
      'internal',
      typeof msg === 'string' ? msg : JSON.stringify(msg)
    );
  }
});

/* ========================================================================
   QRIS DIRECT API — CHECK PAYMENT STATUS
   ======================================================================== */

/**
 * Callable function to check payment status.
 * Expected input:
 *  {
 *     orderId: "string"
 *  }
 */
export const checkPaymentStatus = functions.https.onCall(async (data, _context) => {
  try {
    console.log('===== [checkPaymentStatus] CALLED =====');
    console.log('RAW INPUT:', data);

    const body = data.data || {};
    const orderId = body.orderId;

    console.log('Parsed orderId:', orderId);

    if (!orderId) {
      console.error('Missing orderId');
      throw new Error('Missing orderId');
    }

    const serverKey = process.env.MIDTRANS_SERVER_KEY;
    if (!serverKey) {
      console.error('Server key missing');
      throw new Error('Midtrans server key is not set');
    }

    const url = `https://api.sandbox.midtrans.com/v2/${orderId}/status`;

    console.log('[checkPaymentStatus] Calling Midtrans…');
    console.log('URL:', url);
    console.log('Auth header:', authHeader());

    const response = await axios.get(url, {
      headers: {
        Authorization: authHeader(),
      },
    });

    console.log('[checkPaymentStatus] Midtrans STATUS Response:', response.data);

    return response.data;
  } catch (err: any) {
    const msg = err?.response?.data || err?.message || 'Unknown error';

    console.error('[checkPaymentStatus] ERROR:', msg);

    throw new functions.https.HttpsError(
      'internal',
      typeof msg === 'string' ? msg : JSON.stringify(msg)
    );
  }
});
