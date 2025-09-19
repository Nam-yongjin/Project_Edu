const ItemModal = ({ maxQty, value, onChange, onConfirm, onClose }) => {
    const maxAllowed = Math.min(maxQty, 30);  // 빌릴 수 있는 최대 허용치

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-white p-6 rounded-xl shadow-lg w-[320px] space-y-4 min-blank">
                <h2 className="newText-xl font-bold text-center">수량 입력</h2>
                <input
                    type="number"
                    min={1}
                    max={maxAllowed}
                    value={value}
                    onChange={(e) => onChange(Number(e.target.value))}
                    className="w-full border p-2 rounded text-center newText-lg"
                />
                <div className="flex justify-end gap-3 pt-2">
                    <button
                        onClick={onClose}
                        className="px-4 py-2 rounded border normal-button"
                    >
                        취소
                    </button>
                    <button
                        onClick={() => {
                            if (value < 1) {
                                alert("수량은 1개 이상이어야 합니다.");
                                return;
                            }

                            if (value > 30) {
                                alert("한 회원당 30개까지 대여 가능합니다.");
                                return;
                            }

                            if (value > maxQty) {
                                alert(`재고 수량은 ${maxQty}개입니다.`);
                                return;
                            }

                            onConfirm();

                        }}
                        className="px-4 py-2 rounded positive-button"
                    >
                        확인
                    </button>
                </div>
            </div>
        </div>
    );
};


export default ItemModal;
