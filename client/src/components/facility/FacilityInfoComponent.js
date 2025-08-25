import React from "react";

const FacilityInfoComponent = () => {
  const features = [
    {
      title: "맞춤형 공간 제공",
      desc: `행사 성격과 규모에 맞는 다양한 공간(소규모 세미나실, 강의실, 대강당 등)를 제공합니다.
필요에 따라 원하는 공간을 선택할 수 있어 효율적이고 전문적인 행사가 가능합니다.`,
    },
    {
      title: "교육·행사 직접 활용",
      desc: `대여자는 강의, 발표, 세미나, 워크숍 등 다양한 활동을 현장에서 바로 운영할 수 있습니다.
최신 장비와 쾌적한 환경을 기반으로 참가자들의 집중도와 행사 만족도를 높일 수 있습니다.`,
    },
    {
      title: "상호 신뢰 기반 협력",
      desc: `이용자와 기관은 긴밀히 소통하여 최적의 공간 활용 방안을 함께 모색합니다.
이를 통해 행사 품질을 높이고, 장기적인 협력 관계로 발전할 수 있습니다.`,
    },
  ];

  return (
    <div className="max-w-screen-xl mx-auto my-10">
      <div className="min-blank">
        {/* 제목 */}
        <h1 className="newText-3xl font-bold text-gray-900 mb-10 text-center tracking-wide border-b-2 border-orange-400 pb-3">
          공간 대여 프로그램
        </h1>

        {/* 설명 */}
        <p className="newText-lg text-center text-gray-700 mb-14 max-w-3xl mx-auto leading-relaxed">
          에듀테크 소프트랩은 교육, 세미나, 워크숍, 컨퍼런스 등
          다양한 활동이 원활히 진행될 수 있도록 최적의 공간을 제공합니다.
          이용자들이 최신 시설과 편의 서비스를 활용할 수 있도록 지원하며,
          쾌적하고 전문적인 교육·행사 환경을 조성합니다.
        </p>

        {/* 특징 카드 */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-10">
          {features.map(({ title, desc }, i) => (
            <div
              key={i}
              className="shadow-lg rounded-2xl p-6 border border-orange-200 bg-[#FAF5EF] hover:shadow-2xl transition-shadow duration-300"
            >
              <h3 className="newText-xl font-semibold mb-4 text-orange-600">
                {title}
              </h3>
              <p className="newText-base text-gray-700 whitespace-pre-line">
                {desc}
              </p>
            </div>
          ))}
        </div>

        {/* 하단 안내 */}
        <p className="mt-16 text-center text-gray-600 newText-base max-w-3xl mx-auto leading-relaxed">
          공간 대여 프로그램은 단순한 공간 제공을 넘어,
          교육과 행사의 성공을 위한 최적의 환경과 서비스를 함께 지원합니다.
          여러분의 적극적인 참여와 관심을 기다리며,
          언제든 문의해 주시면 상세히 안내해 드리겠습니다.
        </p>
      </div>
    </div>
  );
};

export default FacilityInfoComponent;