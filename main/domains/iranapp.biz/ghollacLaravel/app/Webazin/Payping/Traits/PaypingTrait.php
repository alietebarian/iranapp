<?php


namespace App\Webazin\Payping\Traits;


trait PaypingTrait {
	protected $paymentUrl = 'https://api.payping.ir/v2/pay';
	protected $verifyUrl = 'https://api.payping.ir/v2/pay/verify';
	protected $redirectUrl = 'https://api.payping.ir/v2/pay/gotoipg/';
	protected $shabaUrl = 'https://api.payping.ir/v1/withdraw/refund';
	protected $retutnUrl = 'http://iranapp.biz/api/webazin/discount/pay/callback';
	protected $token = '1a1b38b522574e58c4b6c086b52c9f3c879ab9e804199913e650ce29a7eae61f';
	protected $amount = 0;
	protected $shabaAmount = 0;
	protected $shaba = "";
	protected $payerIdentity = '';
	protected $payerName = '';
	protected $description = '';
	protected $clientRefId = '';
	protected $refId = '';
	protected $code = '';
	protected $paypingRefId = '';


	/**
	 * @return string
	 */
	public function getDescription() {
		return $this->description;
	}

	/**
	 * @param string $description
	 */
	public function setDescription( $description ) {
		$this->description = $description;
	}

	/**
	 * @return string
	 */
	public function getPayerIdentity() {
		return $this->payerIdentity;
	}

	/**
	 * @param string $payerIdentity
	 */
	public function setPayerIdentity( $payerIdentity ) {
		$this->payerIdentity = $payerIdentity;
	}

	/**
	 * @return string
	 */
	public function getPayerName() {
		return $this->payerName;
	}

	/**
	 * @param string $payerName
	 */
	public function setPayerName( $payerName ) {
		$this->payerName = $payerName;
	}

	/**
	 * @return string
	 */
	public function getClientRefId() {
		return $this->clientRefId;
	}

	/**
	 * @param string $clientRefId
	 */
	public function setClientRefId( $clientRefId ) {
		$this->clientRefId = $clientRefId;
	}

	/**
	 * @return int
	 */
	public function getAmount() {
		return $this->amount;
	}

	/**
	 * @param int $amount
	 */
	public function setAmount( $amount ) {
		$this->amount = $amount;
	}

	/**
	 * @return string
	 */
	public function getRefId() {
		return $this->refId;
	}

	/**
	 * @param string $refId
	 */
	public function setRefId( $refId ) {
		$this->refId = $refId;
	}

	/**
	 * @return string
	 */
	public function getRetutnUrl() {
		return $this->retutnUrl;
	}

	/**
	 * @param string $retutnUrl
	 */
	public function setRetutnUrl( $retutnUrl ) {
		$this->retutnUrl = $retutnUrl;
	}

	/**
	 * @return string
	 */
	public function getShaba() {
		return $this->shaba;
	}

	/**
	 * @param string $shaba
	 */
	public function setShaba( $shaba ) {
		$this->shaba = $shaba;
	}

	/**
	 * @return int
	 */
	public function getShabaAmount() {
		return $this->shabaAmount;
	}

	/**
	 * @param int $shabaAmount
	 */
	public function setShabaAmount( $shabaAmount ) {
		$this->shabaAmount = $shabaAmount;
	}

	/**
	 * @return string
	 */
	public function getPaypingRefId() {
		return $this->paypingRefId;
	}

	/**
	 * @param string $paypingRefId
	 */
	public function setPaypingRefId( $paypingRefId ) {
		$this->paypingRefId = $paypingRefId;
	}
}
